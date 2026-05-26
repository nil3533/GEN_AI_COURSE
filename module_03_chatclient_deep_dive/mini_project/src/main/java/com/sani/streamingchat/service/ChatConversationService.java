package com.sani.streamingchat.service;

import com.sani.streamingchat.config.AiChatProperties;
import com.sani.streamingchat.dto.ChatReply;
import com.sani.streamingchat.dto.ChatRequest;
import com.sani.streamingchat.dto.ChatStreamEvent;
import com.sani.streamingchat.dto.SessionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatConversationService {

    private static final Logger log = LoggerFactory.getLogger(ChatConversationService.class);

    private final AiChatProperties properties;

    private final ChatGateway chatGateway;

    private final Map<String, List<Message>> sessions = new ConcurrentHashMap<>();

    public ChatConversationService(AiChatProperties properties, ChatGateway chatGateway) {
        this.properties = properties;
        this.chatGateway = chatGateway;
    }

    public ChatReply nonStreaming(ChatRequest request) {
        var prepared = prepare(request);
        var startedAt = System.nanoTime();
        var response = chatGateway.call(prepared.settings(), prepared.context(), prepared.temperature());
        var latencyMs = elapsedMs(startedAt);

        appendSuccessfulTurn(prepared.sessionId(), request.message(), response.answer());

        log.info("llm_call mode=non_streaming provider={} model={} session_id={} latency_ms={} status=success",
                prepared.providerName(), response.model(), prepared.sessionId(), latencyMs);

        return new ChatReply(
                prepared.sessionId(),
                prepared.providerName(),
                response.model(),
                response.answer(),
                latencyMs,
                response.promptTokens(),
                response.completionTokens(),
                response.totalTokens()
        );
    }

    public Flux<ServerSentEvent<ChatStreamEvent>> stream(ChatRequest request) {
        var prepared = prepare(request);
        var startedAt = System.nanoTime();
        var answer = new StringBuilder();

        return Flux.defer(() -> {
                    var start = event("start", ChatStreamEvent.start(
                            prepared.sessionId(),
                            prepared.providerName(),
                            prepared.settings().getModel()));

                    var tokens = chatGateway.stream(prepared.settings(), prepared.context(), prepared.temperature())
                            .map(token -> {
                                answer.append(token);
                                return event("token", ChatStreamEvent.token(
                                        prepared.sessionId(),
                                        prepared.providerName(),
                                        prepared.settings().getModel(),
                                        token));
                            });

                    var done = Mono.fromSupplier(() -> {
                        var latencyMs = elapsedMs(startedAt);
                        appendSuccessfulTurn(prepared.sessionId(), request.message(), answer.toString());
                        log.info("llm_call mode=streaming provider={} model={} session_id={} latency_ms={} status=success",
                                prepared.providerName(), prepared.settings().getModel(), prepared.sessionId(), latencyMs);
                        return event("done", ChatStreamEvent.done(
                                prepared.sessionId(),
                                prepared.providerName(),
                                prepared.settings().getModel(),
                                latencyMs));
                    });

                    return Flux.concat(Flux.just(start), tokens, done);
                })
                .doOnCancel(() -> log.info("llm_call mode=streaming provider={} model={} session_id={} status=cancelled",
                        prepared.providerName(), prepared.settings().getModel(), prepared.sessionId()))
                .onErrorResume(exception -> {
                    var latencyMs = elapsedMs(startedAt);
                    var message = friendlyError(exception);
                    log.warn("llm_call mode=streaming provider={} model={} session_id={} latency_ms={} status=failed error={}",
                            prepared.providerName(), prepared.settings().getModel(), prepared.sessionId(), latencyMs, message);
                    return Mono.just(event("error", ChatStreamEvent.error(
                            prepared.sessionId(),
                            prepared.providerName(),
                            prepared.settings().getModel(),
                            latencyMs,
                            message)));
                });
    }

    public SessionSummary sessionSummary(String sessionId) {
        var history = sessions.get(sessionId);
        var count = history == null ? 0 : history.size();
        return new SessionSummary(sessionId, count, properties.getMaxHistoryMessages());
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public Map<String, Object> providerSummary() {
        var providers = new LinkedHashMap<String, Map<String, Object>>();
        properties.getProviders().forEach((name, settings) -> providers.put(name, Map.of(
                "enabled", settings.isEnabled(),
                "type", nullToEmpty(settings.getType()),
                "model", nullToEmpty(settings.getModel()),
                "baseUrl", nullToEmpty(settings.getBaseUrl()),
                "requiresApiKey", settings.requiresApiKey(),
                "apiKeyConfigured", StringUtils.hasText(settings.getApiKey())
        )));

        return Map.of(
                "activeProvider", properties.getActiveProvider(),
                "persona", properties.getPersona(),
                "maxHistoryMessages", properties.getMaxHistoryMessages(),
                "providers", providers
        );
    }

    private PreparedRequest prepare(ChatRequest request) {
        var sessionId = normalizeSessionId(request.sessionId());
        var providerName = properties.getActiveProvider();
        var settings = providerSettings(providerName);

        validateProvider(providerName, settings);

        var context = new PromptContext(
                sessionId,
                request.message(),
                properties.getSystemPromptTemplate(),
                properties.getPersona(),
                LocalDate.now().toString(),
                historySnapshot(sessionId)
        );

        return new PreparedRequest(sessionId, providerName, settings, context, request.temperature());
    }

    private AiChatProperties.ProviderSettings providerSettings(String providerName) {
        var settings = properties.getProviders().get(providerName);
        if (settings == null) {
            throw new ChatProviderConfigurationException("Unknown provider: " + providerName);
        }
        return settings;
    }

    private void validateProvider(String providerName, AiChatProperties.ProviderSettings settings) {
        if (!settings.isEnabled()) {
            throw new ChatProviderConfigurationException("Active provider '" + providerName + "' is disabled.");
        }
        if (!StringUtils.hasText(settings.getType())) {
            throw new ChatProviderConfigurationException("Provider '" + providerName + "' is missing type.");
        }
        if (!StringUtils.hasText(settings.getModel())) {
            throw new ChatProviderConfigurationException("Provider '" + providerName + "' is missing model.");
        }
        if (!StringUtils.hasText(settings.getBaseUrl())) {
            throw new ChatProviderConfigurationException("Provider '" + providerName + "' is missing baseUrl.");
        }
        if (settings.requiresApiKey() && !StringUtils.hasText(settings.getApiKey())) {
            throw new ChatProviderConfigurationException("Provider '" + providerName + "' requires an API key environment variable.");
        }
    }

    private List<Message> historySnapshot(String sessionId) {
        var history = sessions.computeIfAbsent(sessionId, ignored -> Collections.synchronizedList(new ArrayList<>()));
        synchronized (history) {
            var fromIndex = Math.max(0, history.size() - properties.getMaxHistoryMessages());
            return List.copyOf(history.subList(fromIndex, history.size()));
        }
    }

    private void appendSuccessfulTurn(String sessionId, String userMessage, String assistantMessage) {
        if (!StringUtils.hasText(assistantMessage)) {
            return;
        }

        var history = sessions.computeIfAbsent(sessionId, ignored -> Collections.synchronizedList(new ArrayList<>()));
        synchronized (history) {
            history.add(new UserMessage(userMessage));
            history.add(new AssistantMessage(assistantMessage.trim()));
            while (history.size() > properties.getMaxHistoryMessages()) {
                history.removeFirst();
            }
        }
    }

    private String normalizeSessionId(String sessionId) {
        return StringUtils.hasText(sessionId) ? sessionId.trim() : UUID.randomUUID().toString();
    }

    private ServerSentEvent<ChatStreamEvent> event(String eventName, ChatStreamEvent data) {
        return ServerSentEvent.<ChatStreamEvent>builder(data)
                .event(eventName)
                .build();
    }

    private String friendlyError(Throwable exception) {
        if (exception instanceof ChatProviderConfigurationException
                || exception instanceof SpringAiChatGateway.ChatProviderConfigurationException
                || exception instanceof SpringAiChatGateway.ChatProviderCallException) {
            return exception.getMessage();
        }
        return exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public static class ChatProviderConfigurationException extends RuntimeException {

        public ChatProviderConfigurationException(String message) {
            super(message);
        }
    }

    private record PreparedRequest(
            String sessionId,
            String providerName,
            AiChatProperties.ProviderSettings settings,
            PromptContext context,
            Double temperature
    ) {
    }
}
