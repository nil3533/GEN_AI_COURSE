# 2.3 - application.yml Driven Multi-Provider Setup

> Module 2 - File 3 of 8 - Switch providers by configuration, not code

## The Simple Idea

Provider switching should usually be a deployment decision. The service code should depend on `ChatClient`; `application.yml` and Spring profiles should decide which provider backs that client.

```text
Same controller
Same service
Same ChatClient usage
Different active profile
Different provider configuration
```

## Infographic

![Provider switching with Spring profiles](assets/provider-profile-switch.svg)

## Recommended File Layout

```text
src/main/resources/
  application.yml
  application-groq.yml
  application-ollama.yml
```

`application.yml` contains app defaults:

```yaml
spring:
  application:
    name: m02-multi-provider-chat

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

Each provider profile contains only provider-specific settings.

## Groq Through OpenAI-Compatible Configuration

Spring AI integrates with Groq through the OpenAI client because Groq exposes an OpenAI-compatible API surface.

```yaml
# application-groq.yml
spring:
  ai:
    model:
      chat: openai
    openai:
      api-key: ${GROQ_API_KEY:}
      base-url: https://api.groq.com/openai
      chat:
        options:
          model: llama-3.3-70b-versatile
          temperature: 0.3
          max-tokens: 800
```

Important difference from Module 1: raw HTTP used a direct `/openai/v1` URL. Spring AI's OpenAI client expects the configured provider base URL and handles its own path construction.

Run with:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=groq
```

## Ollama Local Profile

For your current 8 GB RAM setup, use the small local model you already pulled:

```yaml
# application-ollama.yml
spring:
  ai:
    model:
      chat: ollama
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2:3b
          temperature: 0.2
```

Run Ollama first:

```bash
F:\Ollama\ollama.exe serve
F:\Ollama\ollama.exe run llama3.2:3b
```

Then run the app:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=ollama
```

## Why Profiles Beat If/Else

Avoid this pattern:

```java
if (provider.equals("groq")) {
    callGroq(question);
} else if (provider.equals("ollama")) {
    callOllama(question);
}
```

That makes provider choice a business-code problem. Profiles keep provider choice in configuration, which is easier to test and deploy.

## When One App Needs Multiple Providers at Once

The `/compare` mini-project endpoint will need multiple providers in one running app. That is more advanced than a single active profile. You can solve it by defining multiple `ChatClient` beans and using qualifiers:

```java
@Service
class CompareService {
    private final ChatClient groq;
    private final ChatClient ollama;

    CompareService(
            @Qualifier("groqChatClient") ChatClient groq,
            @Qualifier("ollamaChatClient") ChatClient ollama) {
        this.groq = groq;
        this.ollama = ollama;
    }
}
```

Start with profile switching for `/ask`. Add multiple named clients only when building `/compare`.

## Checklist

Before blaming Spring AI, check:

1. Is the correct profile active?
2. Is the model starter dependency on the classpath?
3. Is `spring.ai.model.chat` set to the intended provider?
4. Is the API key present in the environment?
5. Is the model name valid for that provider today?
6. Is Ollama actually running for local calls?

## Official Docs to Check

- Groq chat with Spring AI: `https://docs.spring.io/spring-ai/reference/api/chat/groq-chat.html`
- Ollama chat with Spring AI: `https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html`
