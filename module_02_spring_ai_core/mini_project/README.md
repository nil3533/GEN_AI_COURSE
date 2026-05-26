# Mini-Project 2: Groq and Ollama Chat with Spring AI

> Module 2 capstone - Spring AI `ChatClient` with Groq and Ollama

## Goal

This project rebuilds Module 1's `/ask` endpoint using Spring AI instead of raw `RestClient`. The application code talks to `ChatClient`; provider details live in profile configuration.

## Endpoints

| Endpoint | Purpose |
|---|---|
| `POST /ask` | Calls the active provider from `app.ai.active-provider` |
| `POST /compare` | Calls every enabled provider and returns side-by-side results |
| `GET /providers` | Shows active provider and safe provider configuration status |
| `GET /actuator/health` | Confirms the app is running |

Request body:

```json
{
  "question": "Explain Spring AI ChatClient in 3 bullets"
}
```

Response shape:

```json
{
  "provider": "ollama",
  "model": "llama3.2:3b",
  "answer": "...",
  "latencyMs": 1234,
  "promptTokens": 42,
  "completionTokens": 88,
  "totalTokens": 130,
  "error": null
}
```

Token fields can be `null` when a provider does not return usage metadata.

## Run Tests

```powershell
cd F:\GEN_AI_COURSE\module_02_spring_ai_core\mini_project
mvn test
```

Latest validation:

- 2026-05-26: `mvn test` passed locally, 4 tests run, 0 failures.

## Run with Ollama

Use this first because it does not need an API key.

```powershell
F:\Ollama\ollama.exe serve
```

In another terminal:

```powershell
cd F:\GEN_AI_COURSE\module_02_spring_ai_core\mini_project
mvn spring-boot:run -Dspring-boot.run.profiles=ollama
```

Test:

```powershell
curl.exe -X POST http://localhost:8080/ask `
  -H "Content-Type: application/json" `
  -d "{\"question\":\"Explain Spring AI ChatClient in 3 bullets\"}"
```

## Run with Groq

Set the key in your shell, not in code:

```powershell
$env:GROQ_API_KEY="your_key_here"
cd F:\GEN_AI_COURSE\module_02_spring_ai_core\mini_project
mvn spring-boot:run -Dspring-boot.run.profiles=groq
```

The default Groq model is:

```text
llama-3.3-70b-versatile
```

## Compare Providers

Compare mode enables Ollama by default. Enable hosted providers only after setting their keys:

```powershell
$env:APP_AI_OLLAMA_ENABLED="true"
$env:APP_AI_GROQ_ENABLED="true"
$env:GROQ_API_KEY="your_key_here"

cd F:\GEN_AI_COURSE\module_02_spring_ai_core\mini_project
mvn spring-boot:run -Dspring-boot.run.profiles=compare
```

Then:

```powershell
curl.exe -X POST http://localhost:8080/compare `
  -H "Content-Type: application/json" `
  -d "{\"question\":\"Compare RestClient and ChatClient in Spring AI\"}"
```

Each provider returns its own result. If one provider fails, the endpoint still returns the other results and puts the failure in that provider's `error` field.

## Key Files

```text
src/main/resources/application.yml
src/main/resources/application-ollama.yml
src/main/resources/application-groq.yml
src/main/resources/application-compare.yml
src/main/java/com/sani/multichat/service/AiChatService.java
src/main/java/com/sani/multichat/controller/ChatController.java
```

## What This Teaches

- how `ChatClient` replaces raw provider HTTP code
- how provider settings move into profiles
- how one endpoint can run on local Ollama or hosted Groq
- how to expose latency and token metadata
- how to design `/compare` so provider failures are isolated
