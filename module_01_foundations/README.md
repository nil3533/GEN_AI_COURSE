# Module 1 — Foundations of Generative AI (for the Spring Engineer)

> **Weeks 1–2 · ~16 hours total**

---

## What you'll walk away with

A clear mental model of: what an LLM actually is, what a transformer actually does, why GenAI differs from "traditional" ML, what tokens/embeddings/context-windows mean in practice, and how Responsible AI affects what you build. By the end you will have written a **Spring Boot service that calls a real LLM via raw `RestClient`** — no Spring AI yet, just HTTP. That's deliberate — you'll appreciate Spring AI's abstractions in Module 2 only after you've felt the pain of raw HTTP.

This is the conceptual foundation for everything else. Don't skip it.

---

## Learning hours breakdown

| Activity | Hours |
|---|---|
| Reading concept files (this folder) | 6 |
| Watching curated videos | 3 |
| Typing out and running code examples | 4 |
| Mini-project (Spring Boot + RestClient + LLM) | 2 |
| Interview prep & weekly note | 1 |
| **Total** | **16** |

Plan: 8 hrs/week × 2 weeks. Adjust to your schedule.

---

## Files in this module (read in order)

1. `01_evolution_ai_ml_dl_genai.md` — Where GenAI fits in the bigger AI family tree; why we "orchestrate, not train"
2. `02_transformers_intuition.md` — The architecture that powers every LLM, explained without math
3. `03_llms_pretrained_vs_finetuned.md` — What "pre-trained" means; when fine-tuning is worth it
4. `04_tokens_embeddings_context.md` — The three concepts you'll use every day as an AI Engineer
5. `05_responsible_ai_and_safety.md` — Bias, harm, safety, guardrails for production systems
6. `06_calling_an_llm_from_java.md` — RestClient + HttpInterface, the OpenAI-compatible API convention, request/response anatomy

---

## Mini-project: "Your first LLM call from Spring Boot"

**Goal:** Build a tiny Spring Boot service that exposes `POST /ask` and calls an LLM (Groq) via raw `RestClient`. No Spring AI. No frameworks.

**Deliverable:** `mini_project/` — a runnable Maven project that:
- Accepts a JSON body `{"question": "..."}` on `POST /ask`
- Calls Groq's OpenAI-compatible chat completions endpoint
- Returns `{"answer": "...", "promptTokens": N, "completionTokens": N, "latencyMs": N}`
- Handles errors: missing API key, rate limit (429), timeout, connection failure
- Reads the API key from `${GROQ_API_KEY}` env var via `application.yml`

**Constraints:** No `spring-ai-*` dependencies. Just `spring-boot-starter-web`. Use `RestClient` (introduced in Boot 3.2). The point is to feel the raw HTTP shape so Module 2's abstraction makes sense.

Stretch goals: add an Actuator custom metric for "llm.calls.total" and "llm.tokens.prompt"; add a circuit breaker via Resilience4j.

Starter pom + walkthrough in `mini_project/README.md`.

---

## Curated videos for this module (free, ~3 hrs)

1. **Andrej Karpathy — "Intro to Large Language Models" (1 hour)**
   `youtube.com/watch?v=zjkBMFhNj_g`
   Single best free intro. Watch it twice.
2. **3Blue1Brown — "But what is a neural network?" + "Attention in transformers"**
   YouTube channel `@3blue1brown` → "Neural Networks" playlist
   Visual intuition for what's happening mathematically.
3. **Jay Alammar — "The Illustrated Transformer" (blog, ~20 min)**
   `jalammar.github.io/illustrated-transformer`
   Most-shared transformer explainer on the internet, for good reason.
4. **Optional but excellent — Dan Vega's "Spring AI Crash Course"**
   YouTube → "Dan Vega Spring AI"
   Watch only **after** you finish file 06. It'll preview Module 2 and motivate why Spring AI exists.

---

## Interview prep — by end of Module 1 you should answer these cold

1. What's the difference between AI, ML, Deep Learning, and Generative AI?
2. What is a transformer? What problem did it solve that RNNs couldn't?
3. What is self-attention? Why is it the key innovation?
4. What is a token? How does tokenization work?
5. What is the difference between pre-training and fine-tuning?
6. What is the context window and why does it matter for application design?
7. **Java-specific:** Walk me through what happens when your Spring Boot service calls OpenAI's API. What's in the request? In the response? What can go wrong?

If any feel shaky after the module, re-read the relevant concept file before moving to Module 2.

See `interview_prep.md` (to be added) for full answers.

---

## Weekly note (at end of Week 2)

Create `weekly_note.md` in this folder. Answer:
- What's the biggest thing I learned this module?
- What still confuses me?
- What would I tell past-me to focus on?

This becomes raw material for interview storytelling in Module 12.

---

Ready? Open `01_evolution_ai_ml_dl_genai.md`.
