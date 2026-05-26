package com.sani.firstllm.service;

import com.sani.firstllm.config.GroqProperties;
import com.sani.firstllm.dto.AskResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    private final RestClient restClient;
    private final GroqProperties properties;

    public LlmService(RestClient groqRestClient, GroqProperties properties) {
        this.restClient = groqRestClient;
        this.properties = properties;
    }

    public AskResponse ask(String question) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY is not set. Add it to your environment and restart the app.");
        }

        var requestBody = Map.of(
                "model", properties.model(),
                "temperature", 0.3,
                "max_tokens", 800,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are a helpful technical assistant for a senior backend engineer. "
                                        + "Be precise, practical, and direct. Prefer short code examples when useful."
                        ),
                        Map.of("role", "user", "content", question)
                )
        );

        var startedAt = System.nanoTime();

        try {
            var response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(GroqChatResponse.class);

            var latencyMs = (System.nanoTime() - startedAt) / 1_000_000;
            var answer = extractAnswer(response);
            var usage = extractUsage(response);

            log.info("LLM call completed model={} prompt_tokens={} completion_tokens={} latency_ms={}",
                    properties.model(), usage.promptTokens(), usage.completionTokens(), latencyMs);

            return new AskResponse(
                    answer,
                    usage.promptTokens(),
                    usage.completionTokens(),
                    latencyMs,
                    properties.model()
            );
        } catch (HttpClientErrorException exception) {
            throw mapClientError(exception);
        } catch (HttpServerErrorException exception) {
            log.error("Groq server error status={}", exception.getStatusCode());
            throw new LlmCallException("LLM provider is unavailable. Try again later.", exception);
        } catch (ResourceAccessException exception) {
            log.error("Groq connection error: {}", exception.getMessage());
            throw new LlmCallException("Could not reach LLM provider.", exception);
        } catch (RestClientException exception) {
            log.error("Unexpected Groq client error: {}", exception.getMessage());
            throw new LlmCallException("Unexpected LLM provider response.", exception);
        }
    }

    private RuntimeException mapClientError(HttpClientErrorException exception) {
        log.error("Groq client error status={} body={}", exception.getStatusCode(), exception.getResponseBodyAsString());

        if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return new LlmRateLimitException("Groq rate limit hit. Try again in a minute.");
        }
        if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return new IllegalStateException("Invalid GROQ_API_KEY.");
        }

        return new LlmCallException("LLM client error: " + exception.getStatusCode(), exception);
    }

    private String extractAnswer(GroqChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new LlmCallException("LLM provider returned no choices.");
        }

        var message = response.choices().getFirst().message();
        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new LlmCallException("LLM provider returned an empty answer.");
        }

        return message.content().trim();
    }

    private Usage extractUsage(GroqChatResponse response) {
        if (response == null || response.usage() == null) {
            return new Usage(0, 0);
        }

        return new Usage(response.usage().promptTokens(), response.usage().completionTokens());
    }

    public static class LlmCallException extends RuntimeException {

        public LlmCallException(String message) {
            super(message);
        }

        public LlmCallException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class LlmRateLimitException extends RuntimeException {

        public LlmRateLimitException(String message) {
            super(message);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GroqChatResponse(
            List<Choice> choices,
            UsageResponse usage
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Choice(
            Message message
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Message(
            String content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record UsageResponse(
            @JsonProperty("prompt_tokens")
            int promptTokens,
            @JsonProperty("completion_tokens")
            int completionTokens
    ) {
    }

    private record Usage(
            int promptTokens,
            int completionTokens
    ) {
    }
}
