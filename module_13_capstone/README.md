# Module 13 — Capstone: Spring Boot Migration Assistant

> **Weeks 19–24 · ~80 hours · The "show, don't tell" deliverable**

> **This is the project you'll talk about in every interview for the next two years.** Spend 80 hours. Don't half-ass it.

---

## What this project is

A production-quality Spring Boot application that ingests a legacy Java/Spring monolith codebase and produces a structured microservices migration plan — built using everything you've learned. RAG over code, agentic planning + critique, MCP tools, full observability, React frontend.

## Why this is the right capstone for you specifically

1. **Domain authority.** You've worked with Spring Boot monoliths and microservices for 12 years. You know what a *good* migration plan looks like. You can judge your AI's output critically. Most candidates can't do that for their projects — they hand-wave the quality.
2. **Nobody else will have this.** Hiring managers see 100 "I built a RAG chatbot over PDFs" capstones. Yours will be specific, technically deep, in your domain. It will be remembered.
3. **Uses every module's skills.** RAG (M5), tool calling (M4), agents + MCP (M7), workflows (M8), Advisors (M6), observability (M9), React (M10).
4. **Architecturally interesting.** Code chunks aren't text chunks — you need AST-aware splitting, dependency-graph awareness. This makes for great interview discussion.
5. **Solves a real business problem.** Every Indian enterprise has a Spring Boot monolith to modernize. This is genuinely useful, not academic.

---

## What it does (the user story)

A staff engineer at a fictional company points the tool at their Spring Boot monolith Git repo. The tool:

1. **Ingests** the codebase — parses Java sources via JavaParser, extracts classes, methods, dependencies, Spring annotations (`@Service`, `@Controller`, `@Repository`, `@Component`), HTTP endpoints, Kafka producers/consumers, DB tables touched
2. **Builds a knowledge base** — embeds each class+context into pgvector with rich metadata (package, annotations, dependencies, business domain hints)
3. **Maps the architecture** — produces a dependency graph (classes, packages, modules)
4. **Plans the migration** — agentic workflow: planner identifies candidate bounded contexts → analyzer assesses each → critic challenges the proposed boundaries → refiner produces final boundaries
5. **Generates artifacts** — for each proposed microservice:
   - A README with the boundary rationale
   - A list of classes to extract
   - Identified shared-state risks (DB tables touched by multiple proposed services)
   - Suggested API contracts between the new services
   - A migration risk score and suggested order
6. **Visualizes** the plan in a React UI — dependency graph, proposed service map, drill-down per service

## Suggested architecture

```
┌──────────────────────────────────────────────────────────────────┐
│  React UI (M10 stack)                                             │
│  - Upload zip / paste git URL                                     │
│  - View dependency graph, proposed services                       │
│  - Drill into class-level recommendations                         │
│  - Stream agentic progress in real-time                           │
└─────────────────────────┬────────────────────────────────────────┘
                          │ REST + SSE
┌─────────────────────────▼────────────────────────────────────────┐
│  Spring Boot 3.5 + Spring AI 2.0                                  │
│                                                                   │
│  ┌──────────────────────┐    ┌─────────────────────────────────┐ │
│  │ Ingestion Service     │    │  LangGraph-style Workflow       │ │
│  │ - Git clone / unzip   │───▶│  (M8 patterns)                  │ │
│  │ - JavaParser AST      │    │   ┌──────────────────────────┐ │ │
│  │ - Build dep graph     │    │   │ Planner LLM              │ │ │
│  │ - Chunk class+context │    │   │  → bounded contexts      │ │ │
│  │ - Embed → pgvector    │    │   └──────────┬───────────────┘ │ │
│  └───────────┬──────────┘    │              ▼                  │ │
│              │               │   ┌──────────────────────────┐ │ │
│              ▼               │   │ Analyzer LLM (per ctx)   │ │ │
│  ┌──────────────────────┐    │   │  uses RAG over codebase  │ │ │
│  │ pgvector store       │◀───┼───┤  uses dep-graph tool     │ │ │
│  │ - Code chunks        │    │   └──────────┬───────────────┘ │ │
│  │ - Class metadata     │    │              ▼                  │ │
│  │ - Dep graph table    │    │   ┌──────────────────────────┐ │ │
│  └──────────────────────┘    │   │ Critic LLM               │ │ │
│                              │   │  → flags issues          │ │ │
│  ┌──────────────────────┐    │   └──────────┬───────────────┘ │ │
│  │ Tools (@Tool)        │    │              ▼                  │ │
│  │ - getDependencies()  │◀───┤   ┌──────────────────────────┐ │ │
│  │ - findUsages()       │    │   │ Refiner LLM              │ │ │
│  │ - getTablesTouched() │    │   │  → final plan            │ │ │
│  └──────────────────────┘    │   └──────────────────────────┘ │ │
│                              └─────────────────────────────────┘ │
│                                                                   │
│  ┌──────────────────────┐    ┌─────────────────────────────────┐ │
│  │ Provider Abstraction │    │  Observability (M9)              │ │
│  │ - Claude (primary)   │    │  - Micrometer metrics            │ │
│  │ - Groq (fallback)    │    │  - OTel traces                   │ │
│  │ - Ollama (local)     │    │  - Cost per run                  │ │
│  └──────────────────────┘    └─────────────────────────────────┘ │
│                                                                   │
│  Workflow state persisted to Postgres — runs survive restarts    │
└──────────────────────────────────────────────────────────────────┘
```

## Deliverables

- **Public GitHub repo** with clean README, the diagram above, setup instructions, screenshots/gifs
- **Deployed demo** (Cloudflare Pages frontend + free-tier Spring Boot host)
- **5-minute Loom video** walking through the system
- **One technical blog post** explaining one decision in depth (e.g., "Why AST-aware chunking for code RAG beats token-based chunking")
- **3 Architecture Decision Records (ADRs)** in `docs/adr/`: vector store choice, agent framework choice, provider strategy
- **Demo dataset** — use a public Spring Boot sample as test input (e.g., `spring-petclinic-microservices`, `online-boutique`, or a real BFSI-style sample)
- **Real metrics in the README** — token cost per run, latency, retrieval quality on test cases

## Week-by-week breakdown

- **Week 19 — Scope & ingestion:** Architecture diagram, repo skeleton, JavaParser-based ingestion working end-to-end on `spring-petclinic`. Class+context chunking storing to pgvector. Dep-graph extraction.
- **Week 20 — RAG layer:** Embeddings + retrieval + reranking working. First LLM answers about the codebase ("which classes touch the `OWNERS` table?").
- **Week 21 — Agentic workflow:** LangGraph-style workflow with planner → analyzer → critic → refiner. State persistence. Stream progress via SSE.
- **Week 22 — Spring AI hardening:** Multi-provider failover. Observability via Micrometer + Grafana dashboard. Resilience4j circuit breakers. Cost tracking. Testcontainers integration tests.
- **Week 23 — React UI + deploy:** Frontend rendering dep graph, proposed services, per-service drill-down. Cloudflare Pages deploy. Free-tier Spring Boot host.
- **Week 24 — Polish + share:** Loom video. README polish. Blog post. ADRs written. Share on LinkedIn, r/java, r/SpringBoot, Hacker News.

## Alternative capstones (if migration doesn't excite you)

All preserve the property: deeply technical, in your domain, no one else has it.

- **Kafka Event Schema Migration Agent** — ingests Avro/JSON schemas + consumer code, suggests safe schema evolutions with auto-generated migration code
- **SQL Performance Advisor** — RAG over PostgreSQL/DB2 docs + EXPLAIN plan analysis + LLM-generated optimization suggestions, packaged as a Spring Boot service
- **Microservices Sprawl Auditor** — ingests an organization's service catalog (OpenAPI specs + READMEs), identifies redundant services and API contract violations using agents
- **Compliance-Aware Code Review Bot** — RAG over your org's coding guidelines + security policies + an agent that reviews PRs for violations
- **Spring Boot Upgrade Assistant** — ingests a Boot 2.x or 3.x codebase and produces a Boot 4.x migration plan with breaking-change mitigations

---

## After the capstone

You're done with the structured curriculum. Now:

1. Share the capstone aggressively — LinkedIn, X/Twitter, r/java, r/SpringBoot, Hacker News if brave
2. Apply to roles using the capstone as your lead story, not a side note
3. Subscribe to ongoing learning: Spring AI release notes, Anthropic / OpenAI engineering blogs, Latent Space podcast, Simon Willison's blog
4. Consider speaking — internal tech talks → meetups (Bangalore/Hyderabad have active GenAI meetups) → conferences
5. Stay humble. The field moves fast. The fundamentals you learned here are durable. The specific tools will change.

You're a Java AI Engineer now. A rare and valuable one. Go build.
