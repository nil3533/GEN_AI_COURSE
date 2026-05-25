# Mini-Project 1: Your First LLM Call from Spring Boot

> **Module 1 capstone exercise · ~2 hours · No Spring AI yet — just raw `RestClient`**

---

## The challenge

Build a Spring Boot service that exposes `POST /ask` and calls an LLM (Groq). Use only `spring-boot-starter-web` and `RestClient` — no `spring-ai-*` dependencies. The point: feel what an LLM API call looks like at the HTTP level so Module 2's Spring AI abstractions make sense.

By the end you'll have:
- A running Spring Boot service
- A real LLM call working end-to-end
- An understanding of request/response shape, error modes, and the three metrics that matter (prompt tokens, completion tokens, latency)

---

## Step 1 — Generate the project (5 min)

Use Spring Initializr:

```bash
mkdir -p ~/code/sani-genai-journey
cd ~/code/sani-genai-journey

curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.5.0 \
  -d javaVersion=21 \
  -d groupId=com.sani \
  -d artifactId=m01-first-llm-call \
  -d name=first-llm-call \
  -d packageName=com.sani.firstllm \
  -d dependencies=web,actuator,validation \
  -o m01.zip

unzip m01.zip -d m01-first-llm-call
rm m01.zip
cd m01-first-llm-call
```

Open in your IDE.

---

## Step 2 — Set up configuration (5 min)

`src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: m01-first-llm-call

groq:
  base-url: https://api.groq.com/openai/v1
  api-key: ${GROQ_API_KEY:}    # reads from env var; empty default
  model: llama-3.1-70b-versatile
  timeout-seconds: 30

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

Make sure `GROQ_API_KEY` is set in your environment (see `02_ENVIRONMENT_SETUP.md`).

---

## Step 3 — Try it yourself first (45 min)

**Before reading the reference code below**, sketch out:
- A `@ConfigurationProperties` class for the Groq settings
- A `RestClient` bean configured with the base URL and timeout
- A `LlmService` that builds the request body and parses the response
- A `@RestController` exposing `POST /ask`
- A `@ControllerAdvice` for friendly error responses

You'll learn more from 45 minutes of struggle than 45 minutes of reading. Then compare with the reference.

---

## Step 4 — Reference implementation

### Configuration properties

`src/main/java/com/sani/firstllm/config/GroqProperties.java`:

```java
package com.sani.firstllm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "groq")
public record GroqProperties(
        String baseUrl,
        String apiKey,
        String model,
        int timeoutSeconds
) {}
```

Wire it up — add to your main application class:

```java
package com.sani.firstllm;

import com.sani.firstllm.config.GroqProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GroqProperties.class)
public class FirstLlmCallApplication {
    public static void main(String[] args) {
        SpringApplication.run(FirstLlmCallApplication.class, args);
    }
}
```

### RestClient bean

`src/main/java/com/sani/firstllm/config/RestClientConfig.java`:

```java
package com.sani.firstllm.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient groqRestClient(GroqProperties props) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(props.timeoutSeconds()));

        return RestClient.builder()
                .baseUrl(props.baseUrl())
                .requestFactory(factory)
                .defaultHeader("Authorization", "Bearer " + props.apiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
```

### Request and response DTOs

`src/main/java/com/sani/firstllm/dto/AskRequest.java`:

```java
package com.sani.firstllm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AskRequest(
        @NotBlank @Size(max = 4000)
        String question
) {}
```

`src/main/java/com/sani/firstllm/dto/AskResponse.java`:

```java
package com.sani.firstllm.dto;

public record AskResponse(
        String answer,
        int promptTokens,
        int completionTokens,
        long latencyMs,
        String model
) {}
```

### LLM service — the heart of the project

`src/main/java/com/sani/firstllm/service/LlmService.java`:

```java
package com.sani.firstllm.service;

import com.sani.firstllm.config.GroqProperties;
import com.sani.firstllm.dto.AskResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    private final RestClient restClient;
    private final GroqProperties props;

    public LlmService(RestClient groqRestClient, GroqProperties props) {
        this.restClient = groqRestClient;
        this.props = props;
    }

    public AskResponse ask(String question) {
        if (props.apiKey() == null || props.apiKey().isBlank()) {
            throw new IllegalStateException(
                "GROQ_API_KEY not set. Run: export GROQ_API_KEY=your_key");
        }

        // Build the OpenAI-format request body
        // (Groq is OpenAI-API-compatible)
        var requestBody = Map.of(
            "model", props.model(),
            "temperature", 0.3,
            "max_tokens", 800,
            "messages", List.of(
                Map.of(
                    "role", "system",
                    "content", "You are a helpful technical assistant for a senior backend engineer. " +
                               "Be precise. Prefer code examples over prose when relevant. " +
                               "If you're unsure about something, say so explicitly."
                ),
                Map.of("role", "user", "content", question)
            )
        );

        var start = System.currentTimeMillis();

        try {
            // Make the HTTP call
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            var latencyMs = System.currentTimeMillis() - start;

            // Parse the OpenAI-format response
            @SuppressWarnings("unchecked")
            var choices = (List<Map<String, Object>>) response.get("choices");
            @SuppressWarnings("unchecked")
            var message = (Map<String, Object>) choices.get(0).get("message");
            var content = ((String) message.get("content")).trim();

            @SuppressWarnings("unchecked")
            var usage = (Map<String, Object>) response.get("usage");
            var promptTokens = ((Number) usage.get("prompt_tokens")).intValue();
            var completionTokens = ((Number) usage.get("completion_tokens")).intValue();

            log.info("LLM call: model={} prompt_tokens={} completion_tokens={} latency_ms={}",
                    props.model(), promptTokens, completionTokens, latencyMs);

            return new AskResponse(content, promptTokens, completionTokens, latencyMs, props.model());

        } catch (HttpClientErrorException e) {
            // 4xx — most commonly 401 (bad key), 429 (rate limit), 400 (bad request)
            log.error("Groq client error: status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatusCode.valueOf(429)) {
                throw new LlmRateLimitException("Groq rate limit hit. Try again in a minute.");
            }
            if (e.getStatusCode() == HttpStatusCode.valueOf(401)) {
                throw new IllegalStateException("Invalid GROQ_API_KEY.");
            }
            throw new LlmCallException("LLM client error: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            // 5xx — Groq side issue
            log.error("Groq server error: status={}", e.getStatusCode());
            throw new LlmCallException("LLM provider unavailable", e);
        } catch (ResourceAccessException e) {
            // Network/timeout
            log.error("Groq connection error: {}", e.getMessage());
            throw new LlmCallException("Could not reach LLM provider", e);
        }
    }

    public static class LlmCallException extends RuntimeException {
        public LlmCallException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class LlmRateLimitException extends RuntimeException {
        public LlmRateLimitException(String msg) { super(msg); }
    }
}
```

### Controller

`src/main/java/com/sani/firstllm/controller/AskController.java`:

```java
package com.sani.firstllm.controller;

import com.sani.firstllm.dto.AskRequest;
import com.sani.firstllm.dto.AskResponse;
import com.sani.firstllm.service.LlmService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

    private final LlmService llmService;

    public AskController(LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return llmService.ask(request.question());
    }
}
```

### Error handler

`src/main/java/com/sani/firstllm/controller/GlobalExceptionHandler.java`:

```java
package com.sani.firstllm.controller;

import com.sani.firstllm.service.LlmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LlmService.LlmRateLimitException.class)
    public ResponseEntity<Map<String, String>> rateLimit(LlmService.LlmRateLimitException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(LlmService.LlmCallException.class)
    public ResponseEntity<Map<String, String>> llmError(LlmService.LlmCallException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> illegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation failed: " + e.getMessage()));
    }
}
```

---

## Step 5 — Run it

```bash
# In one terminal:
./mvnw spring-boot:run

# In another:
curl -X POST http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"Explain the difference between Spring Boot and Spring Framework in 3 sentences"}'
```

Expected response (formatted):
```json
{
  "answer": "Spring Framework is the foundational Java framework...",
  "promptTokens": 65,
  "completionTokens": 84,
  "latencyMs": 1247,
  "model": "llama-3.1-70b-versatile"
}
```

The three numbers — **promptTokens, completionTokens, latencyMs** — are the metrics you'll measure in every production GenAI system. Burn them into your brain.

---

## Step 6 — Verify your understanding

Without scrolling back, answer:

1. What does `temperature: 0.3` do? What would happen at `1.5`?
2. Why is there a `system` message separate from the `user` message?
3. If I called this 1000 times/minute, what breaks first?
4. The library doesn't say "Groq" anywhere — how does our code know to call Groq?
5. What does `prompt_tokens` count, and why care?
6. Where exactly does `RestClient` deserialize JSON to `Map`? What happens if Groq returns malformed JSON?
7. If `GROQ_API_KEY` env var is unset, where does it fail and with what error?

If you can answer all seven cold, you understand the LLM API surface. If not, re-read the relevant code section.

---

## Step 7 — Write a tiny test (15 min)

Spring AI in Module 2 will introduce slick testing. For now, write a basic test using `MockRestServiceServer` to mock Groq's response — proves you can test LLM-calling code without hitting the real API.

`src/test/java/com/sani/firstllm/service/LlmServiceTest.java` — sketch this yourself, or save for Module 9 when we cover testing properly.

---

## Step 8 — Push to GitHub

```bash
cd ~/code/sani-genai-journey
git add m01-first-llm-call/
git commit -m "M01: first LLM call from Spring Boot via raw RestClient"
git push
```

Add this project to the root `README.md` of your repo with a one-line description.

---

## Stretch goals (optional, recommended)

If you finish with time, try these — they preview ideas from later modules:

1. **Multi-turn conversation.** Track a `sessionId` → list of past messages. (Foreshadows Module 6 — chat memory.)
2. **Try a different model.** Swap `llama-3.1-70b-versatile` for `llama-3.1-8b-instant`. Compare quality, speed, token cost. (Module 2.)
3. **Add streaming.** Use `RestClient` with `bodyToFlux` (Reactor) to stream the response token-by-token. (Module 3 — SSE streaming.)
4. **Switch providers.** Get a Google AI Studio key and modify the service to call Gemini instead. Notice how the request shape differs. (Motivates Spring AI's abstraction in Module 2.)
5. **Add Resilience4j circuit breaker.** Open the circuit on consecutive 5xx responses. (Module 9 — resilience.)

---

## What this exercise taught you

- The mechanics of an LLM API call from Java: request shape, response shape, parameters
- The OpenAI-compatible API convention that lets Groq, OpenRouter, and many others use the same client code
- Three production metrics: prompt tokens, completion tokens, latency
- Error handling for real failure modes (rate limits, bad keys, connection issues, timeouts)
- Why we use `system` vs `user` message separation (foreshadows Module 3 prompt engineering)
- Spring's existing patterns (`@ConfigurationProperties`, `RestClient`, `@ControllerAdvice`, validation) apply directly to LLM work

You've now done what 90% of "GenAI tutorial" readers don't — shipped working Java code that talks to a real LLM. **And you did it without Spring AI.** When you meet Spring AI in Module 2, you'll know exactly what it's saving you.

→ When done, mark Module 1 complete and move to `module_02_spring_ai_core/`.
