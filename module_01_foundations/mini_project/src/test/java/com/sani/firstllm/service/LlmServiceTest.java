package com.sani.firstllm.service;

import com.sani.firstllm.config.GroqProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class LlmServiceTest {

    private static final String BASE_URL = "https://api.groq.test/openai/v1";

    private MockRestServiceServer server;
    private LlmService llmService;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer test-key");
        server = MockRestServiceServer.bindTo(builder).build();
        llmService = new LlmService(builder.build(), new GroqProperties(BASE_URL, "test-key", "test-model", 30));
    }

    @Test
    void returnsAnswerAndUsageWhenProviderResponds() {
        server.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "role": "assistant",
                                "content": "Spring Boot simplifies Spring application setup."
                              }
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 11,
                            "completion_tokens": 7,
                            "total_tokens": 18
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        var response = llmService.ask("Explain Spring Boot.");

        assertThat(response.answer()).isEqualTo("Spring Boot simplifies Spring application setup.");
        assertThat(response.promptTokens()).isEqualTo(11);
        assertThat(response.completionTokens()).isEqualTo(7);
        assertThat(response.latencyMs()).isGreaterThanOrEqualTo(0);
        assertThat(response.model()).isEqualTo("test-model");
        server.verify();
    }

    @Test
    void throwsHelpfulErrorWhenApiKeyMissing() {
        var service = new LlmService(RestClient.create(BASE_URL), new GroqProperties(BASE_URL, "", "test-model", 30));

        assertThatThrownBy(() -> service.ask("hello"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("GROQ_API_KEY");
    }

    @Test
    void mapsRateLimitResponseToDomainException() {
        server.expect(requestTo(BASE_URL + "/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "error": {
                                    "message": "rate limit exceeded"
                                  }
                                }
                                """));

        assertThatThrownBy(() -> llmService.ask("hello"))
                .isInstanceOf(LlmService.LlmRateLimitException.class)
                .hasMessageContaining("rate limit");
        server.verify();
    }
}
