# Module 3 — ChatClient Deep Dive: Prompts, Streaming, Multi-Turn

> **Week 5 · ~10 hours**

---

## What you'll walk away with

Mastery of Spring AI's prompt construction, the four message types (`SystemMessage`, `UserMessage`, `AssistantMessage`, `ToolResponseMessage`), `PromptTemplate` with variable interpolation, and — critically — **streaming responses via Server-Sent Events (SSE)** so your UI shows tokens as they arrive instead of making the user wait 10 seconds for a full reply. Streaming is the difference between "demo feel" and "ChatGPT feel" in your applications.

You'll also learn prompt engineering techniques (zero-shot, few-shot, chain-of-thought, role prompting) in the Spring AI idiom.

---

## Files (outline)

1. `01_anatomy_of_a_prompt.md` — Messages, roles, system prompt placement, what the LLM actually sees
2. `02_prompt_templates_and_interpolation.md` — `PromptTemplate`, `TemplateRenderer`, StringTemplate vs Mustache, escaping pitfalls
3. `03_prompt_engineering_techniques_in_spring_ai.md` — Zero-shot, few-shot examples, chain-of-thought, role prompting — implemented as `PromptTemplate`s
4. `04_streaming_responses_with_sse.md` — `ChatClient.prompt().stream().content()` returning `Flux<String>`; exposing via Spring WebFlux + SSE
5. `05_multi_turn_conversations_no_memory_yet.md` — Passing message history manually before we meet `ChatMemory` in Module 6
6. `06_chatoptions_per_request.md` — Per-call overrides, provider-specific options (response_format, top_p, frequency_penalty)
7. `07_handling_long_outputs_truncation.md` — `max_tokens`, response truncation detection, continuation strategies

## Mini-project

**"Streaming chat REST API."** Build a Spring Boot service with `POST /api/chat/stream` returning a `text/event-stream` SSE response that streams the LLM's reply token-by-token. Support:
- A session-scoped message history (just a `ConcurrentHashMap<sessionId, List<Message>>` for now — proper memory comes in Module 6)
- A `PromptTemplate`-based system prompt with the date and a configurable persona
- Per-request temperature override
- Graceful client disconnect handling
- A `/chat/non-streaming` endpoint for comparison so you can feel the UX difference

Test it from `curl --no-buffer` or a tiny HTML page. You'll wire this to React in Module 10.

**Push to:** `sani-genai-journey/m03-streaming-chat-api/`

## Curated free resources

- Spring AI Reference — "ChatClient API" section, "Streaming" subsection
- Spring docs — "Server-Sent Events" in Spring Web MVC and WebFlux
- Anthropic Prompt Engineering Guide (free, language-agnostic) — `docs.anthropic.com/en/docs/build-with-claude/prompt-engineering`

## Interview prep highlights

- "Why use streaming for LLM responses? What's the UX and engineering trade-off vs. waiting for the full response?"
- "How does Server-Sent Events work? Why is it preferred over WebSockets for LLM streaming?"
- "Walk me through `PromptTemplate` rendering — what happens if a user injects `{` characters?"
- "Compare zero-shot, few-shot, and chain-of-thought prompting with concrete examples."
- "Your streaming endpoint sometimes hangs after 30 seconds. Debug it."
