package com.sani.orderassistant.service;

import com.sani.orderassistant.config.AiAssistantProperties;
import com.sani.orderassistant.dto.AssistantRequest;
import com.sani.orderassistant.dto.AssistantResponse;
import com.sani.orderassistant.tools.OrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.stereotype.Component;

@Component
public class SpringAiAssistantGateway implements AssistantGateway {

    private final ToolCallbackProvider orderToolProvider;

    public SpringAiAssistantGateway(OrderTools orderTools) {
        this.orderToolProvider = MethodToolCallbackProvider.builder()
                .toolObjects(orderTools)
                .build();
    }

    @Override
    public AssistantResponse generate(AssistantRequest request, AiAssistantProperties.ProviderSettings settings) {
        var converter = new BeanOutputConverter<>(AssistantResponse.class);

        return createChatClient(settings).prompt()
                .system(system -> system
                        .text("""
                        You are a customer order assistant for a Spring Boot demo shop.
                        Use tools when the user asks about order status, recent orders, or cancellation.
                        Do not invent order data. If tools return no data, say so.
                        Never claim an order was cancelled unless the cancelOrder tool returned status CANCELLED.
                        If cancellation was rejected because confirmation is missing, ask the user to confirm.
                        The final answer must be valid JSON matching this format:
                        {format}
                        """)
                        .param("format", converter.getFormat()))
                .user(userPrompt(request))
                .toolCallbacks(orderToolProvider)
                .call()
                .entity(converter);
    }

    private String userPrompt(AssistantRequest request) {
        return """
                Customer id: %s
                Cancellation confirmed by API flag: %s
                User message: %s
                """.formatted(request.customerId(), request.confirmed(), request.message());
    }

    private ChatClient createChatClient(AiAssistantProperties.ProviderSettings settings) {
        return switch (settings.getType()) {
            case "openai-compatible" -> createOpenAiCompatibleClient(settings);
            case "ollama" -> createOllamaClient(settings);
            default -> throw new AssistantProviderConfigurationException("Unsupported provider type: " + settings.getType());
        };
    }

    private ChatClient createOpenAiCompatibleClient(AiAssistantProperties.ProviderSettings settings) {
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

    private ChatClient createOllamaClient(AiAssistantProperties.ProviderSettings settings) {
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

    public static class AssistantProviderConfigurationException extends RuntimeException {

        public AssistantProviderConfigurationException(String message) {
            super(message);
        }
    }
}
