# Module 2 — Spring AI Core & Multi-Provider Setup

> **Weeks 3–4 · ~16 hours**

---

## What you'll walk away with

Fluency with **Spring AI's `ChatClient`** — the fluent API that replaces all the raw HTTP code you wrote in Module 1's mini-project. You'll be able to swap LLM providers (OpenAI ↔ Anthropic ↔ Gemini ↔ Ollama ↔ Groq) by changing `application.yml`, with zero code changes. You'll know when to use which model and how to make defensible architecture decisions about LLM provider strategy.

This is where Spring AI's "Spring conventions for AI" thesis pays off and your 12 years of Spring expertise becomes superpowers.

---

## Files in this module (outline — full content built as you reach the module)

1. `01_what_is_spring_ai_and_why_it_exists.md` — The thesis, the design philosophy, the comparison with LangChain4j and going framework-free
2. `02_chatclient_the_fluent_api.md` — Deep dive into `ChatClient.Builder`, `prompt().user().call().content()`, fluent vs. low-level `ChatModel`
3. `03_application_yml_driven_multi_provider.md` — Spring Profiles + provider-specific properties; the elegance of `spring.ai.openai.base-url`
4. `04_provider_comparison_2026.md` — Cost, latency, capability, free-tier landscape — OpenAI, Anthropic, Gemini, Groq, Ollama, Mistral
5. `05_running_local_llms_with_ollama.md` — Spring AI's `OllamaChatModel`, when local wins (privacy, cost at scale, dev iteration)
6. `06_chatoptions_temperature_max_tokens_etc.md` — Per-request options, defaults, provider-specific options (function calling, JSON mode, etc.)
7. `07_observability_at_the_chatclient_level.md` — Token counts, latency, model used — all exposed via Spring AI metrics
8. `08_when_to_skip_spring_ai.md` — Honest discussion of when raw HTTP (or LangChain4j) is the right call

## Mini-project

**"Multi-provider chat service."** Rebuild Module 1's `/ask` endpoint using Spring AI's `ChatClient`. The same endpoint must work against:
- Groq (Llama 3.1 70B)
- Ollama (local Llama 3.1 8B)
- Google Gemini (free tier)
- (optional) OpenAI GPT-4o-mini if you have a key

Switching is done via Spring Profile (`-Dspring.profiles.active=ollama`), not code changes. Add a `/compare` endpoint that fans out the same question to all configured providers and returns a side-by-side comparison of answer, latency, and token cost. This is a real benchmarking tool you'll reuse.

**Push to:** `sani-genai-journey/m02-multi-provider-chat/`

## Curated free resources

- Spring AI Reference — "Getting Started" + "ChatClient API" sections (`docs.spring.io/spring-ai/reference`)
- Dan Vega — "Spring AI Crash Course" YouTube (latest version)
- Josh Long — "Spring Tips: Spring AI" episode (search YouTube)
- Spring AI GitHub examples — the `chat-client/` and `models/` folders (`github.com/spring-projects/spring-ai-examples`)

## Interview prep highlights

- "Walk me through Spring AI's `ChatClient` API end-to-end."
- "How would you design a system that can swap LLM providers based on a config flag?"
- "When would you use Spring AI vs LangChain4j vs rolling your own?"
- "Your Spring AI app currently uses GPT-4. Compliance now requires India-resident processing. Walk me through the migration."
- "Compare prompt tuning, prompt engineering, and fine-tuning."
- "What's the difference between `ChatClient` and `ChatModel` in Spring AI? When do you use each?"
