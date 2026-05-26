package com.sani.ragdocs.answer;

import com.sani.ragdocs.config.RagProperties;
import com.sani.ragdocs.store.RetrievedChunk;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnExpression("'${app.rag.chat-provider:template}' == 'spring-ai-ollama' || '${app.rag.chat-provider:template}' == 'spring-ai-groq'")
public class SpringAiAnswerGateway implements AnswerGateway {

    private final RagProperties properties;

    public SpringAiAnswerGateway(RagProperties properties) {
        this.properties = properties;
    }

    @Override
    public String answer(String question, List<RetrievedChunk> context) {
        if (context == null || context.isEmpty()) {
            return "I do not have enough retrieved context to answer this question.";
        }

        var answer = createChatClient().prompt()
                .system("""
                        You are a RAG assistant for a Spring AI course project.
                        Answer only from the supplied context.
                        If the context is not enough, say you do not have enough information.
                        Keep the answer concise and mention the source document ids you used.
                        Do not invent facts or external references.
                        """)
                .user("""
                        Question:
                        %s

                        Retrieved context:
                        %s
                        """.formatted(question, contextBlock(context)))
                .call()
                .content();

        if (!StringUtils.hasText(answer)) {
            throw new AnswerProviderException("Provider returned an empty answer.");
        }

        return answer.trim();
    }

    private String contextBlock(List<RetrievedChunk> context) {
        return context.stream()
                .map(chunk -> """
                        [documentId=%s, title=%s, source=%s, chunk=%d, score=%.3f]
                        %s
                        """.formatted(
                        chunk.documentId(),
                        chunk.title(),
                        chunk.source(),
                        chunk.chunkIndex(),
                        chunk.relevanceScore(),
                        chunk.content()))
                .collect(Collectors.joining("\n---\n"));
    }

    private ChatClient createChatClient() {
        return switch (properties.getChatProvider()) {
            case "spring-ai-ollama" -> createOllamaClient();
            case "spring-ai-groq" -> createGroqClient();
            default -> throw new AnswerProviderException("Unsupported chat provider: " + properties.getChatProvider());
        };
    }

    private ChatClient createOllamaClient() {
        var api = OllamaApi.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .build();

        var options = OllamaChatOptions.builder()
                .model(properties.getOllama().getChatModel())
                .temperature(0.1)
                .build();

        var chatModel = OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(options)
                .build();

        return ChatClient.create(chatModel);
    }

    private ChatClient createGroqClient() {
        var groq = properties.getGroq();
        if (!StringUtils.hasText(groq.getApiKey())) {
            throw new AnswerProviderException("GROQ_API_KEY is required when app.rag.chat-provider=spring-ai-groq.");
        }

        var api = OpenAiApi.builder()
                .baseUrl(groq.getBaseUrl())
                .apiKey(groq.getApiKey())
                .build();

        var options = OpenAiChatOptions.builder()
                .model(groq.getModel())
                .temperature(groq.getTemperature())
                .maxTokens(groq.getMaxTokens())
                .build();

        var chatModel = OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();

        return ChatClient.create(chatModel);
    }

    public static class AnswerProviderException extends RuntimeException {

        public AnswerProviderException(String message) {
            super(message);
        }
    }
}
