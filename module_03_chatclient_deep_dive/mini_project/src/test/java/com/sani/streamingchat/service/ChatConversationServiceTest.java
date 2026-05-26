package com.sani.streamingchat.service;

import com.sani.streamingchat.config.AiChatProperties;
import com.sani.streamingchat.dto.ChatRequest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatConversationServiceTest {

    @Test
    void nonStreamingAppendsSuccessfulTurnToHistory() {
        var gateway = new FakeGateway();
        var service = new ChatConversationService(properties(), gateway);

        var response = service.nonStreaming(new ChatRequest("session-1", "What is SSE?", 0.2));

        assertThat(response.sessionId()).isEqualTo("session-1");
        assertThat(response.answer()).isEqualTo("fake answer");
        assertThat(service.sessionSummary("session-1").messageCount()).isEqualTo(2);
        assertThat(gateway.contexts).hasSize(1);
        assertThat(gateway.contexts.getFirst().history()).isEmpty();
    }

    @Test
    void secondTurnReceivesPriorHistory() {
        var gateway = new FakeGateway();
        var service = new ChatConversationService(properties(), gateway);

        service.nonStreaming(new ChatRequest("session-1", "First question", null));
        service.nonStreaming(new ChatRequest("session-1", "Follow up", null));

        assertThat(gateway.contexts).hasSize(2);
        assertThat(gateway.contexts.get(1).history()).hasSize(2);
    }

    @Test
    void streamingAppendsHistoryOnlyAfterDone() {
        var gateway = new FakeGateway();
        var service = new ChatConversationService(properties(), gateway);

        StepVerifier.create(service.stream(new ChatRequest("stream-1", "Stream this", 0.1))
                        .map(event -> event.data().type()))
                .expectNext("start")
                .expectNext("token")
                .expectNext("token")
                .expectNext("done")
                .verifyComplete();

        assertThat(service.sessionSummary("stream-1").messageCount()).isEqualTo(2);
    }

    @Test
    void streamingFailureDoesNotAppendPartialHistory() {
        var gateway = new FakeGateway();
        gateway.failStreaming = true;
        var service = new ChatConversationService(properties(), gateway);

        StepVerifier.create(service.stream(new ChatRequest("stream-err", "Stream this", null))
                        .map(event -> event.data().type()))
                .expectNext("start")
                .expectNext("error")
                .verifyComplete();

        assertThat(service.sessionSummary("stream-err").messageCount()).isZero();
    }

    private AiChatProperties properties() {
        var properties = new AiChatProperties();
        properties.setActiveProvider("ollama");
        properties.setProviders(new LinkedHashMap<>());
        properties.getProviders().put("ollama", provider("ollama", true));
        return properties;
    }

    private AiChatProperties.ProviderSettings provider(String type, boolean enabled) {
        var provider = new AiChatProperties.ProviderSettings();
        provider.setEnabled(enabled);
        provider.setType(type);
        provider.setBaseUrl("http://localhost:11434");
        provider.setModel("test-model");
        provider.setTemperature(0.2);
        provider.setMaxTokens(100);
        return provider;
    }

    private static class FakeGateway implements ChatGateway {

        private final List<PromptContext> contexts = new ArrayList<>();

        private boolean failStreaming;

        @Override
        public AiChatResponse call(AiChatProperties.ProviderSettings settings, PromptContext context, Double temperature) {
            contexts.add(context);
            return new AiChatResponse("fake answer", settings.getModel(), 1, 2, 3);
        }

        @Override
        public Flux<String> stream(AiChatProperties.ProviderSettings settings, PromptContext context, Double temperature) {
            contexts.add(context);
            if (failStreaming) {
                return Flux.error(new RuntimeException("provider down"));
            }
            return Flux.just("fake", " stream");
        }
    }
}
