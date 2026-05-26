# 2.1 - What Is Spring AI and Why It Exists

> Module 2 - File 1 of 8 - The Spring mental model for GenAI apps

## The Simple Idea

Spring AI brings AI application patterns into normal Spring Boot style. In Module 1 you manually built HTTP JSON, added bearer headers, parsed provider responses, handled errors, and returned an answer. Spring AI does not remove those concepts. It gives you Spring abstractions so most application code can focus on prompts, options, tools, memory, retrieval, and observability.

The important shift:

```text
Module 1:
Controller -> service -> RestClient -> provider-specific HTTP JSON

Module 2:
Controller -> service -> ChatClient -> ChatModel -> configured provider
```

## Infographic

![Spring AI abstraction stack](assets/spring-ai-abstraction-stack.svg)

## Why It Exists

LLM APIs look similar, but they are not identical. Every provider has different model names, option names, error behavior, streaming format, tool calling details, and metadata. If you write all provider code by hand, your business service slowly becomes an API compatibility layer.

Spring AI tries to solve this using familiar Spring ideas:

| Spring idea | Spring AI version |
|---|---|
| `RestClient` / `WebClient` fluent style | `ChatClient` |
| Interface plus implementation | `ChatModel` plus provider model |
| Externalized config | `application.yml` provider properties |
| Boot starters | `spring-ai-starter-model-*` dependencies |
| Actuator/Micrometer | model call observations and metrics |
| Auto-configuration | provider beans created from classpath and properties |

For a Spring engineer, this is the main benefit: you do not need to become a Python framework engineer to build production GenAI services in Java.

## What Spring AI Is Not

Spring AI is not the model. It does not make a weak model smart. It does not make hallucination impossible. It does not replace authorization, validation, rate limiting, logging, or testing.

Think of it as infrastructure around the model call:

- prompt construction
- provider abstraction
- model options
- streaming
- structured output
- tool calling
- chat memory
- retrieval integration
- observability

Those features still need engineering judgment.

## Minimal Dependency Shape

Use the Spring AI BOM and then add the model starter you need. Exact versions move, so check the current Spring AI reference before creating a new project.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-bom</artifactId>
      <version>${spring-ai.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
  </dependency>
</dependencies>
```

For Groq, Spring AI reuses the OpenAI-compatible client with Groq's base URL. For Ollama, use the Ollama starter.

## Spring Engineer View

Your service should not know whether the provider is Groq or Ollama unless the business use case requires that distinction. A normal `/ask` endpoint should depend on `ChatClient`, not on provider HTTP classes.

Good boundary:

```java
@Service
class AiAnswerService {
    private final ChatClient chatClient;

    AiAnswerService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    String answer(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
```

Bad boundary:

```java
// Business code now knows provider URL, header format, JSON shape, and parser rules.
restClient.post()
    .uri("/chat/completions")
    .header("Authorization", "Bearer " + apiKey)
    .body(providerSpecificMap)
    .retrieve()
    .body(Map.class);
```

Raw HTTP is still valid sometimes, but it should be a deliberate choice.

## Mini Exercise

Take your Module 1 `LlmService` and mark each line as one of:

1. business behavior
2. provider transport detail
3. request option
4. response parsing
5. observability/error handling

Spring AI mainly reduces categories 2 and 4. You still own the others.

## Official Docs to Check

- Spring AI ChatClient API: `https://docs.spring.io/spring-ai/reference/api/chatclient.html`
- Spring AI model providers: `https://docs.spring.io/spring-ai/reference/api/chatmodel.html`
