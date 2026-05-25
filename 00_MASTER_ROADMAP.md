# GenAI Engineer Roadmap (Spring AI Edition) — 6-Month Self-Study, Free

> **A complete Spring AI–based replacement for the IIT Patna "Certificate Program in Generative AI & Agentic AI for Developers." Pure Java/Kotlin, Spring Boot REST + React frontends, pgvector for RAG. No Python anywhere.**

---

## Who this is built for

You — Sani. 12+ years of Java, Spring Boot, microservices, Kafka, DB2/PostgreSQL/Oracle, Docker, basic React. You want to become an AI Engineer **without** detouring through Python. That choice is now correct, not eccentric: **Spring AI 2.0 GA ships May 28, 2026 (two days from now)** and ecosystem maturity has crossed the line where Java is a first-class GenAI platform.

**Your positioning after this course:** "Senior backend engineer with 12+ years in Java/Spring Boot, now shipping production GenAI in Spring AI 2.0 — RAG with pgvector, MCP-enabled tools, multi-provider LLM orchestration via Advisors, full observability via Micrometer." That's a Staff/Architect-grade pitch and very rare in the Indian job market.

---

## Why Spring AI is the right bet for you (and the honest trade-offs)

**For you, Spring AI wins because:**

- Reuses everything you know — auto-configuration, starters, beans, profiles, AOP-style cross-cutting, transactions, Micrometer, Resilience4j patterns.
- Vendor-neutral by design — one `ChatClient` interface, swap OpenAI ↔ Anthropic ↔ Gemini ↔ Ollama via `application.yml`. Your existing instinct for clean abstractions transfers directly.
- Production-first — observability, security, resilience, testing are not afterthoughts; they're built in via the Spring conventions you already use.
- pgvector + Spring AI = RAG without leaving Postgres. You already operate Postgres. The vector store is just another table.

**The honest trade-offs vs. Python:**

- **Smaller community** for cutting-edge GenAI research artifacts. New papers ship with Python notebooks first; Java equivalents lag by weeks to months.
- **Fewer pre-built integrations** with the latest experimental tools (some LangChain integrations have no Spring AI parallel yet).
- **You should still be able to read Python** — when you hit a problem Spring AI hasn't solved yet, the answer is often in a Python repo. Reading ≠ writing. We won't write any, but a few hours skimming Python syntax pays for itself many times over.

LangChain4j exists as an alternative Java framework — it's framework-agnostic (works without Spring), supports more providers, and has 200+ contributors including from Google/Red Hat/JetBrains. Spring AI is the better choice for you because you live in Spring Boot. We'll briefly cover LangChain4j in Module 2 so you know when to reach for it.

---

## Versions and stack as of May 2026

| Component | Version we'll use | Notes |
|---|---|---|
| **Java** | 21 (LTS) | Spring Boot 4.x requires 17+, 21 is the safe production default |
| **Spring Boot** | 3.5.x (latest 3.x) | Stable; Spring Boot 4.0 also fine if you prefer bleeding edge |
| **Spring AI** | **2.0 GA** (releasing May 28, 2026) | Falls back to 1.0.8 stable if 2.0 hits launch issues |
| **PostgreSQL** | 16 + pgvector extension | Run via Docker locally |
| **Frontend** | React 19 + Vite | Minimal — REST clients, not heavy UIs |
| **Build** | Maven (we'll show Gradle equivalents in comments) | Your call |
| **LLM providers** | Groq (free), Ollama (local), Gemini (free tier) primary; OpenAI/Anthropic for production examples | ₹0–₹2000 total spend over 6 months |

If Spring AI 2.0 GA has launch-day issues, we start on 1.0.8 stable and upgrade later. The course material flags any 1.x → 2.x breaking changes (Jackson 3 migration, MCP package renames, etc.) where relevant.

---

## The 13-module map

Renamed and re-sequenced to match a Spring-Boot-first learning order.

| # | Module | Duration | What you'll build |
|---|---|---|---|
| 01 | Foundations of GenAI | Weeks 1–2 | A working Spring Boot service that calls an LLM via raw `RestClient` |
| 02 | Spring AI Core & Multi-Provider Setup | Weeks 3–4 | A `ChatClient`-based service that switches LLM providers via profile |
| 03 | ChatClient Deep Dive — Prompts, Streaming, Options | Week 5 | A multi-turn chat REST API with streaming SSE responses |
| 04 | Structured Output & Tool Calling | Weeks 6–7 | A weather/order-status assistant that calls your existing services |
| 05 | RAG with pgvector | Weeks 8–9 | "Chat with your docs" using PostgreSQL + Spring AI document pipeline |
| 06 | Advisors & Chat Memory | Week 10 | Cross-cutting LLM concerns (logging, safety, cost) as Spring AI Advisors |
| 07 | MCP & Agents in Spring AI | Weeks 11–12 | An agent that uses MCP-provided tools to act on your systems |
| 08 | Stateful Workflows & Streaming | Weeks 13–14 | Multi-step LLM pipelines with Spring Integration / state machines |
| 09 | Observability, Resilience & Production Patterns | Week 15 | Micrometer metrics, Resilience4j fallbacks, prompt versioning |
| 10 | React Frontends for AI APIs | Week 16 | A React chat UI consuming your streaming Spring Boot endpoint |
| 11 | AI-Assisted Development | Week 17 | Master Cursor/Claude Code/Copilot patterns for Java/Spring work |
| 12 | Career, Portfolio & Interview Prep | Week 18 | Rewritten resume, polished GitHub, project storytelling |
| 13 | Capstone — Spring Boot Migration Assistant | Weeks 19–24 | Production-grade RAG + agent system in your domain |

**Total: ~120–150 hours of focused work over 24 weeks. ~6 hrs/week minimum, ~10 hrs/week recommended.**

---

## What's different from the IIT Patna curriculum

| IIT Patna Module | What I did |
|---|---|
| 1. Foundations of GenAI | Kept; concepts are language-agnostic. Added a Java-vs-LLM-orchestration mental model. |
| 2. Working with Models & APIs | Mapped to Spring AI's `ChatClient` + `application.yml`-driven multi-provider config. |
| 3. Apps Without Frameworks | Kept the spirit — first build with raw HTTP from Java, then graduate to Spring AI. |
| 4. RAG Systems | Rebuilt using Spring AI's `VectorStore`/`DocumentReader`/`TokenTextSplitter` + pgvector. |
| 5. LangChain Essentials | **Replaced with Spring AI Advisors & Chat Memory** — the Spring-idiomatic equivalent. |
| 6. Agents, Tools, MCP | Done via Spring AI's `@Tool` beans and first-class MCP support. |
| 7. LangGraph | **Replaced with Spring AI workflow patterns + Spring Integration** — Java-native stateful flows. |
| 8. Dev Productivity Stack | Kept (Cursor/Claude Code/Copilot are language-agnostic). Reframed examples around Java. |
| 9. CrewAI / n8n / Multi-agent | Reframed as multi-agent patterns inside Spring AI; n8n still useful as orchestration (it calls your Spring endpoints). |
| 10. Career & Portfolio | Kept. Resume positioning shifts to "production Spring AI" — much rarer, much more valuable. |
| (added) React Frontend | New module — you wanted React; we'll keep it minimal and pragmatic. |
| (added) Observability & Resilience | Promoted to its own module because Spring AI deserves it (this is your edge). |
| Capstone | Kept — Spring Boot Migration Assistant aligns perfectly with this stack. |

---

## The free resource stack — Spring AI edition

### Primary reading (your textbook)

| Resource | What it's for | URL |
|---|---|---|
| **Spring AI Reference Docs** | The canonical source. Stay on the 1.1.x / 2.0.x docs | docs.spring.io/spring-ai/reference |
| **Spring AI Examples on GitHub** | Working runnable code for every feature | github.com/spring-projects/spring-ai-examples |
| **Spring AI Source Code** | Read the Advisors, ChatClient, VectorStore internals | github.com/spring-projects/spring-ai |
| **Spring Blog — Spring AI tag** | Release notes, deep dives | spring.io/blog/category/ai |
| **Spring Academy (free tier)** | Free Spring AI courses | spring.academy |
| **Baeldung — Spring AI articles** | Practical tutorials | baeldung.com/spring-ai (search topic) |

### Video / interactive

| Channel | What it's for |
|---|---|
| **Dan Vega (YouTube)** | Spring Developer Advocate; complete Spring AI playlist |
| **Josh Long (YouTube + Spring Tips)** | Spring Tips episodes on Spring AI (very current) |
| **Mark Heckler** | Spring Developer Advocate, Spring AI deep dives |
| **Andrej Karpathy — "Intro to LLMs"** | One-hour foundational video (language-agnostic) |
| **3Blue1Brown — "Neural Networks" series** | Visual intuition for the math |
| **Jay Alammar — "The Illustrated Transformer" (blog)** | The canonical transformer explainer |

### LLM providers (free tiers)

| Provider | Free tier | Use for |
|---|---|---|
| **Groq** | Generous free tier, very fast Llama/Mixtral | Default for all course exercises |
| **Google AI Studio (Gemini)** | Generous free tier | Comparison + multimodal |
| **Ollama** | Free forever, runs on your machine | Privacy/local-first scenarios |
| **OpenAI** | Pay-as-you-go (₹500 covers all course needs) | Production-style examples |
| **Anthropic Claude** | Pay-as-you-go via API | When you want best-in-class reasoning |

Realistic 6-month spend: **₹0 if you stay on Groq + Ollama + Gemini, up to ₹2000 if you sprinkle in OpenAI/Anthropic.**

---

## Project repo layout

Create one GitHub repo: `sani-genai-journey`. Each module gets its own subfolder. By Module 13 your repo is your portfolio.

```
sani-genai-journey/
├── README.md                      ← course progress tracker
├── docs/
│   └── adr/                       ← architecture decision records you'll write
├── m01-first-llm-call/            ← raw RestClient calling an LLM
├── m02-multi-provider-chat/       ← ChatClient + profile-based provider switching
├── m03-streaming-chat-api/        ← SSE streaming chat endpoint
├── m04-tool-calling-assistant/    ← @Tool beans for order status, weather
├── m05-pgvector-rag/              ← Chat with docs using pgvector
├── m06-advisors-memory/           ← Logging/safety/memory Advisors
├── m07-mcp-agent/                 ← MCP-enabled agent
├── m08-workflow-pipelines/        ← Stateful multi-step LLM workflows
├── m09-observability/             ← Micrometer + Resilience4j + cost tracking
├── m10-react-frontend/            ← React chat UI talking to m03/m05 backends
├── m13-capstone/                  ← Spring Boot Migration Assistant
└── shared/                        ← reused Docker Compose, utility classes
```

Each project subfolder follows a strict template:

```
mXX-project-name/
├── README.md              ← problem, architecture, run instructions, screenshots
├── pom.xml                ← Maven build
├── docker-compose.yml     ← Postgres + pgvector, Ollama if needed
├── src/
│   ├── main/java/         ← Spring Boot code
│   ├── main/resources/    ← application.yml, application-{provider}.yml
│   └── test/java/         ← Testcontainers-based integration tests
└── frontend/              ← optional React app
```

---

## Per-topic file template (used by every module's deep-dive files)

Same ten-section structure you've been using for your Java interview prep — proven and consistent:

1. **Why this matters** — real production hook
2. **Concept in plain English**
3. **Deeper mechanics**
4. **Code example** — Java/Spring AI, runnable
5. **Common pitfalls**
6. **When to use / when not**
7. **Comparison table** — with adjacent concepts
8. **Production considerations**
9. **Interview questions** — 3–5
10. **Further reading** — free only

---

## Weekly cadence

- **Mon–Tue evenings (1.5 hrs each):** Read this week's module concept files
- **Wed–Thu evenings (1.5 hrs each):** Type out and run code examples
- **Sat (3–4 hrs):** Build the module's mini-project end-to-end, push to GitHub
- **Sun (1 hr):** Review interview questions, write weekly note

Total: ~10 hrs/week. Protect Saturday — that's where learning consolidates into working code.

---

## Capstone preview — start thinking from Module 4

**Project:** Spring Boot Migration Assistant — a RAG + agent system that ingests a legacy Java/Spring monolith and outputs a structured microservices migration plan.

**Why this is the right capstone for you specifically:**

- Domain authority — you've migrated monoliths. You can judge your AI's output critically (most candidates can't).
- Unique on resumes — no Python-only candidate will have this.
- Uses every module's skills — RAG (M5), tool calling (M4), agents/MCP (M7), workflows (M8), observability (M9), React frontend (M10).
- Architecturally interesting — code chunks aren't text chunks (AST-aware splitting, dependency graphs). Great interview talking points.
- Solves a real problem — every Indian enterprise has a Spring Boot monolith to modernize.

Full design in `module_13_capstone/README.md`.

---

## Interview-ready milestones

By the end of each phase you should be able to answer cold:

- **End of Month 1 (Modules 1–2):** "What is an LLM? How is Spring AI's `ChatClient` different from calling the OpenAI HTTP API directly? When would you skip Spring AI?"
- **End of Month 2 (Modules 3–4):** "Walk me through structured output mapping in Spring AI. How does `@Tool` actually work end-to-end?"
- **End of Month 3 (Modules 5–6):** "Design a RAG system for a 10M-document enterprise KB using Spring Boot. Discuss chunking, embedding model choice, pgvector configuration, retrieval, reranking. Then add safety and logging via Advisors."
- **End of Month 4 (Modules 7–8):** "What is MCP? Why is it preferable to custom function calling? Design a multi-step agentic workflow using Spring AI."
- **End of Month 5 (Modules 9–11):** "How would you ship a Spring AI feature to production — cost, latency, fallbacks, observability, prompt versioning?"
- **End of Month 6 (Capstone):** Walk through the migration assistant end-to-end, defending every architectural choice.

Crisp answers to all of these = ready for Senior/Staff/Architect AI Engineer roles at ₹40L+.

---

## A note on certification

You won't get an IIT Patna certificate from this plan. Real trade-off. Three things to know:

- Hiring managers at strong product companies (Razorpay, Swiggy, PhonePe, Flipkart, Zerodha, Atlassian, Adobe, Google, Microsoft) hire on demonstrated ability. A polished GitHub with 12 working Spring AI projects + a capstone is worth more than most certificates.
- For free credentialing: stack DeepLearning.AI short course certificates (LangChain, RAG, etc. — Python-flavored but concepts transfer) and a Google Generative AI Leader certificate (~₹8000, vendor-respected).
- Spring also offers paid certifications (Spring Professional) — not GenAI-specific yet but adds Spring credibility.

The certificate is the cherry. The portfolio is the cake. Build the cake.

---

## Files in this folder

```
genai_course/
├── 00_MASTER_ROADMAP.md           ← this file
├── 01_GLOSSARY.md                 ← every term you'll encounter
├── 02_ENVIRONMENT_SETUP.md        ← Java 21, Spring Boot, PostgreSQL+pgvector, Docker — one-time setup
├── module_01_foundations/
├── module_02_spring_ai_core/
├── module_03_chatclient_deep_dive/
├── module_04_structured_output_tools/
├── module_05_rag_with_pgvector/
├── module_06_advisors_and_memory/
├── module_07_mcp_and_agents/
├── module_08_workflows_streaming/
├── module_09_observability_resilience/
├── module_10_react_frontend/
├── module_11_dev_productivity/
├── module_12_career_portfolio/
└── module_13_capstone/
```

---

## Ready? Order of operations

1. Read this file fully.
2. Read `01_GLOSSARY.md` once (skim, return as needed).
3. Do the one-time setup in `02_ENVIRONMENT_SETUP.md`.
4. Open `module_01_foundations/README.md` and start.

Let's build.
