# 2.2 - ChatClient: The Fluent API

> Module 2 - File 2 of 8 - The API you will use most often

## The Simple Idea

`ChatClient` is the main Spring AI entry point for chat-style model calls. It feels like `RestClient`: start a fluent chain, build the request, call the remote model, and choose how to read the response.

Basic shape:

```java
String answer = chatClient.prompt()
        .user("Explain Spring Boot Actuator in 3 bullets.")
        .call()
        .content();
```

This replaces most of the manual `RestClient` work from Module 1.

## Infographic

![ChatClient request flow](assets/chatclient-call-flow.svg)

## Creating a ChatClient

In a Boot application, Spring AI can auto-configure a `ChatClient.Builder` when a model starter and valid properties are present.

```java
@RestController
class AskController {
    private final ChatClient chatClient;

    AskController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/ask")
    Map<String, String> ask(@RequestBody AskRequest request) {
        String answer = chatClient.prompt()
                .user(request.question())
                .call()
                .content();

        return Map.of("answer", answer);
    }
}

record AskRequest(String question) {}
```

The controller does not know whether the backing model is Groq or Ollama. That is the point.

## User Message vs System Message

The user message contains the specific task. The system message sets durable behavior.

```java
this.chatClient = builder
        .defaultSystem("""
            You are a concise backend engineering assistant.
            Prefer Java and Spring Boot examples.
            If information is uncertain, say so.
            """)
        .build();
```

Then each request supplies only the task:

```java
String answer = chatClient.prompt()
        .user("Show a controller method for POST /orders.")
        .call()
        .content();
```

Use default system text for app-wide behavior. Use per-call user text for request-specific work.

## Prompt Templates

ChatClient supports runtime parameters in prompt text. Use this when your prompt has a stable structure but variable input.

```java
String answer = chatClient.prompt()
        .user(u -> u
                .text("""
                    Summarize this incident for a senior engineer.
                    Severity: {severity}
                    Notes: {notes}
                    """)
                .param("severity", "P2")
                .param("notes", incidentNotes))
        .call()
        .content();
```

This is safer than string concatenation because it makes the prompt shape explicit.

## Returning More Than Text

For quick demos, `.content()` is enough. For production, you often need metadata.

```java
ChatResponse response = chatClient.prompt()
        .user("Explain token budgeting.")
        .call()
        .chatResponse();
```

Use the richer response when you need model name, finish reason, token usage, or response metadata. Exact metadata varies by provider.

## Streaming

For long answers, streaming improves perceived latency:

```java
Flux<String> stream = chatClient.prompt()
        .user("Write a detailed migration checklist.")
        .stream()
        .content();
```

Do not stream every endpoint by default. Stream when users wait on long generated text. For backend batch jobs, a normal blocking call is often simpler.

## ChatClient vs ChatModel

| Use this | When |
|---|---|
| `ChatClient` | Most application code, fluent prompts, templates, advisors, simple content extraction |
| `ChatModel` | Lower-level model calls, direct `Prompt` objects, custom multiple model wiring |

As a rule: start with `ChatClient`. Drop to `ChatModel` only when the fluent API gets in your way.

## Common Mistakes

- Putting API keys in code instead of environment variables.
- Creating a new `ChatClient` on every request.
- Treating `.content()` as the only useful response shape.
- Mixing business authorization into the system prompt.
- Assuming provider metadata is identical across providers.

## Mini Exercise

Rewrite the Module 1 `/ask` endpoint using only:

```java
ChatClient.Builder
prompt()
user()
call()
content()
```

If your service still manually builds `messages`, `model`, and `Authorization`, you have not crossed the abstraction boundary yet.

## Official Docs to Check

- ChatClient API: `https://docs.spring.io/spring-ai/reference/api/chatclient.html`
