package com.sani.streamingchat.service;

import com.sani.streamingchat.config.AiChatProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Component
public class SpringAiChatGateway implements ChatGateway {

    @Override
    public AiChatResponse call(AiChatProperties.ProviderSettings settings, PromptContext context, Double temperature) {
        var chatClient = createChatClient(settings, temperature);
        var response = chatClient.prompt()
                .system(system -> system
                        .text(context.systemPromptTemplate())
                        .param("persona", context.persona())
                        .param("currentDate", context.currentDate()))
                .messages(context.history())
                .user(context.userMessage())
                .call()
                .chatResponse();

        return toAiChatResponse(response, settings.getModel());
    }

    @Override
    public Flux<String> stream(AiChatProperties.ProviderSettings settings, PromptContext context, Double temperature) {
        var chatClient = createChatClient(settings, temperature);
        return chatClient.prompt()
                .system(system -> system
                        .text(context.systemPromptTemplate())
                        .param("persona", context.persona())
                        .param("currentDate", context.currentDate()))
                .messages(context.history())
                .user(context.userMessage())
                .stream()
                .content();
    }

    private ChatClient createChatClient(AiChatProperties.ProviderSettings settings, Double requestedTemperature) {
        return switch (settings.getType()) {
            case "openai-compatible" -> createOpenAiCompatibleClient(settings, requestedTemperature);
            case "ollama" -> createOllamaClient(settings, requestedTemperature);
            default -> throw new ChatProviderConfigurationException("Unsupported provider type: " + settings.getType());
        };
    }

    private ChatClient createOpenAiCompatibleClient(AiChatProperties.ProviderSettings settings, Double requestedTemperature) {
        var api = OpenAiApi.builder()
                .baseUrl(settings.getBaseUrl())
                .apiKey(settings.getApiKey())
                .build();

        var options = OpenAiChatOptions.builder()
                .model(settings.getModel())
                .temperature(temperature(settings, requestedTemperature))
                .maxTokens(settings.getMaxTokens())
                .build();

        var chatModel = OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();

        return ChatClient.create(chatModel);
    }

    private ChatClient createOllamaClient(AiChatProperties.ProviderSettings settings, Double requestedTemperature) {
        var api = OllamaApi.builder()
                .baseUrl(settings.getBaseUrl())
                .build();

        var options = OllamaChatOptions.builder()
                .model(settings.getModel())
                .temperature(temperature(settings, requestedTemperature))
                .build();

        var chatModel = OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(options)
                .build();

        return ChatClient.create(chatModel);
    }

    private AiChatResponse toAiChatResponse(ChatResponse response, String configuredModel) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            throw new ChatProviderCallException("Provider returned no response.");
        }

        var answer = response.getResult().getOutput().getText();
        if (!StringUtils.hasText(answer)) {
            throw new ChatProviderCallException("Provider returned an empty answer.");
        }

        var metadata = response.getMetadata();
        var usage = metadata != null ? metadata.getUsage() : null;
        var responseModel = metadata != null ? metadata.getModel() : null;
        var model = StringUtils.hasText(responseModel) ? responseModel : configuredModel;

        return new AiChatResponse(
                answer.trim(),
                model,
                token(usage, TokenType.PROMPT),
                token(usage, TokenType.COMPLETION),
                token(usage, TokenType.TOTAL)
        );
    }

    private Double temperature(AiChatProperties.ProviderSettings settings, Double requestedTemperature) {
        return requestedTemperature != null ? requestedTemperature : settings.getTemperature();
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

    private enum TokenType {
        PROMPT,
        COMPLETION,
        TOTAL
    }

    public static class ChatProviderConfigurationException extends RuntimeException {

        public ChatProviderConfigurationException(String message) {
            super(message);
        }
    }

    public static class ChatProviderCallException extends RuntimeException {

        public ChatProviderCallException(String message) {
            super(message);
        }
    }
}
