# Module 6 — Advisors & Chat Memory

> **Week 10 · ~10 hours**

---

## What you'll walk away with

Deep understanding of **Spring AI's killer feature: Advisors** — the AOP-style interception layer for `ChatClient` calls. Plus mastery of `ChatMemory` for managing conversation history. Advisors are where Spring AI most differs from LangChain/LangChain4j and where your existing Spring AOP/interceptor instincts pay off.

By the end, every cross-cutting concern around LLM calls — logging, cost tracking, content safety, retries, memory, RAG — will be modeled as an Advisor chain, exactly as you'd model HTTP interceptors or AOP advice in classic Spring.

---

## Files (outline)

1. `01_what_are_advisors.md` — The AOP parallel; the request/response interception model; why Spring AI chose this design
2. `02_the_advisor_interface_and_lifecycle.md` — `CallAdvisor`, `StreamAdvisor`, `AdvisorChain`, `adviseCall(...)`, `adviseStream(...)`
3. `03_built_in_advisors.md` — `MessageChatMemoryAdvisor`, `QuestionAnswerAdvisor`, `SafeGuardAdvisor`, `SimpleLoggerAdvisor`
4. `04_building_a_custom_logging_advisor.md` — Step-by-step: log every prompt + response + token count to a database
5. `05_building_a_custom_cost_tracking_advisor.md` — Per-tenant cost limits with circuit-breaker behavior on budget exceeded
6. `06_building_a_content_safety_advisor.md` — Pre/post filtering of prompts and responses against rules; integrating with moderation APIs
7. `07_chatmemory_abstraction.md` — `InMemoryChatMemory`, `JdbcChatMemoryRepository` (Postgres-backed) — the schema, the queries
8. `08_message_chat_memory_advisor.md` — How memory is injected into prompts; window sizes; summarization strategies
9. `09_advisor_ordering_and_composition.md` — Order matters: logging outermost, RAG innermost; the assembly idiom
10. `10_testing_advisors.md` — Mocking the chain; unit-testing your custom advisor in isolation

## Mini-project

**"Production-grade chat service with Advisor chain."** Take the streaming chat API from Module 3 and add a full Advisor stack:

1. **LoggingAdvisor** — writes every call to `chat_call_log` table (prompt, response, model, tokens, latency, cost)
2. **CostBudgetAdvisor** — rejects calls exceeding ₹100/day per session
3. **MessageChatMemoryAdvisor** — backed by `JdbcChatMemoryRepository` over Postgres, sliding window of last 20 messages
4. **SafeGuardAdvisor** — blocks responses containing PII patterns (rough regex is fine)
5. **QuestionAnswerAdvisor** — pulls relevant context from your M5 pgvector store

Expose a `/admin/calls` endpoint to view the log. This project alone proves you can build production-grade LLM apps.

**Push to:** `sani-genai-journey/m06-advisors-memory/`

## Curated free resources

- Spring AI Reference — "Advisors" section (the entire thing — read twice)
- Spring AI Reference — "Chat Memory" section
- Spring AI source code — `org.springframework.ai.chat.client.advisor` package on GitHub
- Spring AI examples — `chat-client-advisor/` folder

## Interview prep highlights

- "Explain Spring AI Advisors. Compare them to Spring AOP and to LangChain middleware."
- "Walk me through composing 5 Advisors. What's the execution order and why does it matter?"
- "Design a per-tenant cost budget system using Advisors."
- "How does `MessageChatMemoryAdvisor` interact with the prompt? What does the LLM actually see?"
- "Your conversation memory is unbounded and your token costs are exploding. How do you fix it?"
- "Write a custom Advisor that retries on 429 with exponential backoff."
- "Why is the `ChatMemory` abstraction separate from the `MessageChatMemoryAdvisor`?"
