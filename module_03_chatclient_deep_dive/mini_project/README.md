# Mini-Project 3: Streaming Chat API with Spring AI

> Module 3 capstone - ChatClient prompts, SSE streaming, and short session history

## Goal

This project builds a Spring Boot chat API that can stream LLM output token-by-token and also return a normal JSON response for comparison. It keeps a short in-memory history per `sessionId` so follow-up questions have local context.

## Endpoints

| Endpoint | Purpose |
|---|---|
| `POST /api/chat/stream` | Streams response chunks as Server-Sent Events |
| `POST /api/chat/non-streaming` | Returns the full response after the provider finishes |
| `GET /api/chat/providers` | Shows active provider and safe provider configuration |
| `GET /api/chat/sessions/{sessionId}` | Shows in-memory message count for a session |
| `DELETE /api/chat/sessions/{sessionId}` | Clears one in-memory session |
| `GET /actuator/health` | Confirms the app is running |

Request body:

```json
{
  "sessionId": "m03-demo",
  "message": "Explain SSE streaming in 3 bullets",
  "temperature": 0.2
}
```

`sessionId` is optional. If omitted, the service creates one and returns it in the first event or JSON response.

## Run Tests

```powershell
cd F:\GEN_AI_COURSE\module_03_chatclient_deep_dive\mini_project
mvn test
```

Latest validation:

- 2026-05-26: `mvn test` passed locally, 5 tests run, 0 failures.
- 2026-05-26: Module 3 runtime flow manually tested after the WebFlux blocking-call fix.

## Run with Ollama

Use this first because it does not need an API key.

```powershell
F:\Ollama\ollama.exe serve
```

In another terminal:

```powershell
cd F:\GEN_AI_COURSE\module_03_chatclient_deep_dive\mini_project
mvn spring-boot:run -Dspring-boot.run.profiles=ollama
```

Streaming test:

```powershell
curl.exe --no-buffer -X POST http://localhost:8081/api/chat/stream `
  -H "Content-Type: application/json" `
  -d "{\"sessionId\":\"m03-demo\",\"message\":\"Explain Spring WebFlux SSE in 3 bullets\",\"temperature\":0.2}"
```

Non-streaming comparison:

```powershell
curl.exe -X POST http://localhost:8081/api/chat/non-streaming `
  -H "Content-Type: application/json" `
  -d "{\"sessionId\":\"m03-demo\",\"message\":\"Now summarize that in one sentence\"}"
```

Inspect session history:

```powershell
curl.exe http://localhost:8081/api/chat/sessions/m03-demo
```

## Run with Groq

Set the key in your shell, not in code:

```powershell
$env:GROQ_API_KEY="your_key_here"
cd F:\GEN_AI_COURSE\module_03_chatclient_deep_dive\mini_project
mvn spring-boot:run -Dspring-boot.run.profiles=groq
```

## SSE Event Shape

The streaming endpoint emits these event types:

| Event | Meaning |
|---|---|
| `start` | Session and provider metadata |
| `token` | One streamed text chunk |
| `done` | Completed response with latency |
| `error` | Provider or configuration failure |

Example event payload:

```json
{
  "type": "token",
  "sessionId": "m03-demo",
  "content": "Spring",
  "provider": "ollama",
  "model": "llama3.2:3b",
  "latencyMs": null,
  "error": null
}
```

## Key Files

```text
src/main/resources/application.yml
src/main/resources/application-ollama.yml
src/main/resources/application-groq.yml
src/main/java/com/sani/streamingchat/service/ChatConversationService.java
src/main/java/com/sani/streamingchat/service/SpringAiChatGateway.java
src/main/java/com/sani/streamingchat/controller/ChatController.java
```

## What This Teaches

- how to stream `ChatClient` responses as SSE
- how to build a system prompt with runtime variables
- how to keep bounded manual history before Spring AI memory
- how to expose per-request temperature without exposing every model option
- how to test chat orchestration without calling live LLM APIs
- how to offload blocking provider calls from WebFlux event-loop threads
