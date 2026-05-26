package com.sani.multichat.service;

import com.sani.multichat.config.AiProviderProperties;
import com.sani.multichat.dto.ChatAnswer;
import com.sani.multichat.dto.CompareResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    private final AiProviderProperties properties;

    public AiChatService(AiProviderProperties properties) {
        this.properties = properties;
    }

    public ChatAnswer ask(String question) {
        var providerName = properties.getActiveProvider();
        var settings = providerSettings(providerName);

        if (!settings.isEnabled()) {
            throw new ProviderConfigurationException("Active provider '" + providerName + "' is disabled.");
        }

        var result = callProvider(providerName, settings, question);
        if (!result.success()) {
            throw new ProviderCallException(result.error());
        }

        return result;
    }

    public CompareResponse compare(String question) {
        var enabledProviders = properties.getProviders().entrySet().stream()
                .filter(entry -> entry.getValue().isEnabled())
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .toList();

        if (enabledProviders.isEmpty()) {
            throw new ProviderConfigurationException("No providers are enabled for comparison.");
        }

        var results = enabledProviders.stream()
                .map(entry -> callProvider(entry.getKey(), entry.getValue(), question))
                .toList();

        return new CompareResponse(question, results.size(), results);
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
                "providers", providers
        );
    }

    private ChatAnswer callProvider(String providerName, AiProviderProperties.ProviderSettings settings, String question) {
        var startedAt = System.nanoTime();

        try {
            validateProvider(providerName, settings);

            var chatClient = createChatClient(settings);
            var response = chatClient.prompt()
                    .system(properties.getSystemPrompt())
                    .user(question)
                    .call()
                    .chatResponse();

            var latencyMs = elapsedMs(startedAt);
            var answer = extractAnswer(response);
            var usage = response != null && response.getMetadata() != null
                    ? response.getMetadata().getUsage()
                    : null;
            var responseModel = response != null && response.getMetadata() != null
                    ? response.getMetadata().getModel()
                    : null;
            var model = StringUtils.hasText(responseModel) ? responseModel : settings.getModel();

            log.info("llm_call provider={} model={} latency_ms={} prompt_tokens={} completion_tokens={} status=success",
                    providerName, model, latencyMs, token(usage, TokenType.PROMPT), token(usage, TokenType.COMPLETION));

            return new ChatAnswer(
                    providerName,
                    model,
                    answer,
                    latencyMs,
                    token(usage, TokenType.PROMPT),
                    token(usage, TokenType.COMPLETION),
                    token(usage, TokenType.TOTAL),
                    null
            );
        } catch (Exception exception) {
            var latencyMs = elapsedMs(startedAt);
            var message = friendlyError(providerName, exception);

            log.warn("llm_call provider={} model={} latency_ms={} status=failed error={}",
                    providerName, settings.getModel(), latencyMs, message);

            return ChatAnswer.failed(providerName, settings.getModel(), latencyMs, message);
        }
    }

    private ChatClient createChatClient(AiProviderProperties.ProviderSettings settings) {
        return switch (settings.getType()) {
            case "openai-compatible" -> createOpenAiCompatibleClient(settings);
            case "ollama" -> createOllamaClient(settings);
            default -> throw new ProviderConfigurationException("Unsupported provider type: " + settings.getType());
        };
    }

    private ChatClient createOpenAiCompatibleClient(AiProviderProperties.ProviderSettings settings) {
        var api = OpenAiApi.builder()
                .baseUrl(settings.getBaseUrl())
                .apiKey(settings.getApiKey())
                .build();

        var options = OpenAiChatOptions.builder()
                .model(settings.getModel())
                .temperature(settings.getTemperature())
                .maxTokens(settings.getMaxTokens())
                .build();

        var chatModel = OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();

        return ChatClient.create(chatModel);
    }

    private ChatClient createOllamaClient(AiProviderProperties.ProviderSettings settings) {
        var api = OllamaApi.builder()
                .baseUrl(settings.getBaseUrl())
                .build();

        var options = OllamaChatOptions.builder()
                .model(settings.getModel())
                .temperature(settings.getTemperature())
                .build();

        var chatModel = OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(options)
                .build();

        return ChatClient.create(chatModel);
    }

    private AiProviderProperties.ProviderSettings providerSettings(String providerName) {
        var settings = properties.getProviders().get(providerName);
        if (settings == null) {
            throw new ProviderConfigurationException("Unknown provider: " + providerName);
        }
        return settings;
    }

    private void validateProvider(String providerName, AiProviderProperties.ProviderSettings settings) {
        if (!StringUtils.hasText(settings.getType())) {
            throw new ProviderConfigurationException("Provider '" + providerName + "' is missing type.");
        }
        if (!StringUtils.hasText(settings.getModel())) {
            throw new ProviderConfigurationException("Provider '" + providerName + "' is missing model.");
        }
        if (settings.requiresApiKey() && !StringUtils.hasText(settings.getApiKey())) {
            throw new ProviderConfigurationException("Provider '" + providerName + "' requires an API key environment variable.");
        }
        if (!StringUtils.hasText(settings.getBaseUrl())) {
            throw new ProviderConfigurationException("Provider '" + providerName + "' is missing baseUrl.");
        }
    }

    private String extractAnswer(ChatResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            throw new ProviderCallException("Provider returned no response.");
        }

        var text = response.getResult().getOutput().getText();
        if (!StringUtils.hasText(text)) {
            throw new ProviderCallException("Provider returned an empty answer.");
        }

        return text.trim();
    }

    private Integer token(Usage usage, TokenType tokenType) {
        if (usage == null) {
            return null;
        }

        return switch (tokenType) {
            case PROMPT -> usage.getPromptTokens();
            case COMPLETION -> usage.getCompletionTokens();
            case TOTAL -> usage.getTotalTokens();
        };
    }

    private String friendlyError(String providerName, Exception exception) {
        if (exception instanceof ProviderConfigurationException || exception instanceof ProviderCallException) {
            return exception.getMessage();
        }
        return "Provider '" + providerName + "' failed: " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private enum TokenType {
        PROMPT,
        COMPLETION,
        TOTAL
    }

    public static class ProviderConfigurationException extends RuntimeException {

        public ProviderConfigurationException(String message) {
            super(message);
        }
    }

    public static class ProviderCallException extends RuntimeException {

        public ProviderCallException(String message) {
            super(message);
        }
    }
}
