# Module 9 — Observability, Resilience & Production Patterns

> **Week 15 · ~12 hours**

> **This is your secret weapon module.** Most engineers learn to call LLMs but never learn to *ship* them. Production GenAI lives or dies on observability, resilience, and cost control. Master this and you're operating at a level few others reach.

---

## What you'll walk away with

Production-ready patterns for shipping Spring AI services: Micrometer-based observability, Resilience4j fallbacks and circuit breakers, multi-provider failover, prompt versioning, cost tracking with alerts, and integration test patterns using Testcontainers. By the end you'll have a Spring Boot service that survives provider outages, stays under budget, and gives you full visibility into what the LLM is doing.

---

## Files (outline)

1. `01_what_makes_genai_production_different.md` — New failure modes (hallucination, cost spikes, latency variance) and why classic SRE practices need adjustment
2. `02_spring_ai_observability_via_micrometer.md` — Built-in metrics: `gen_ai.client.operation`, `gen_ai.client.token.usage`; exporting to Prometheus/Grafana
3. `03_structured_logging_for_llm_calls.md` — Logging prompts/responses with redaction; trace IDs across multi-step workflows
4. `04_distributed_tracing_with_otel.md` — Spring AI's OpenTelemetry integration; visualizing a multi-step workflow in Jaeger
5. `05_resilience4j_for_llm_calls.md` — Circuit breakers, retries with backoff, bulkheads, timeouts — applied to `ChatClient`
6. `06_multi_provider_failover.md` — Primary/secondary provider Advisor; degrade gracefully from GPT-4 to Llama on Groq
7. `07_cost_tracking_and_budget_alerts.md` — Per-tenant/per-feature budgets; alerts when approaching limits; daily/monthly caps
8. `08_prompt_versioning.md` — Treating prompts as code: git-tracked, code-reviewed, A/B tested in production
9. `09_testing_llm_apps_with_testcontainers.md` — Local Postgres+pgvector + Ollama in CI; deterministic LLM testing strategies
10. `10_evaluation_in_ci.md` — Adding "does the LLM still answer correctly" checks to your build pipeline
11. `11_security_concerns_specific_to_llms.md` — Prompt injection, jailbreaks, sensitive data leakage; defenses in Spring AI
12. `12_deployment_checklist.md` — A printable checklist before any LLM feature ships to production

## Mini-project

**"Production-hardened version of the M6 chat service."** Take your M6 chat service and add:

1. Micrometer metrics for token counts, latency, cost — exposed at `/actuator/prometheus`
2. Resilience4j circuit breaker on `ChatClient` calls; opens after 5 consecutive 5xx; falls back to a cached "service unavailable" response
3. Multi-provider failover Advisor: try Anthropic → fall back to Groq → fall back to local Ollama
4. Per-session cost budget enforced via Advisor (limit ₹50/session/day)
5. Prompt files stored in `src/main/resources/prompts/v1/system.md` etc., loaded as Spring resources; switch versions via property
6. Testcontainers-based integration test that spins up Postgres+pgvector and runs a real RAG flow
7. A `docker-compose.yml` that also includes Prometheus + Grafana with a pre-built dashboard JSON for LLM metrics

**Push to:** `sani-genai-journey/m09-observability/`

This single project is interview gold. Most candidates can't articulate *any* of these concerns — you'll have shipped them.

## Curated free resources

- Spring AI Reference — "Observability" section (the entire thing)
- Resilience4j docs — `resilience4j.readme.io` (you may already know this)
- "Building LLM-powered applications" — Anthropic engineering blog series
- Simon Willison's blog — `simonwillison.net` — best running commentary on LLM security/operations

## Interview prep highlights

- "Walk me through observability for a Spring AI service in production."
- "Design multi-provider failover for an LLM-powered service. What are the edge cases?"
- "How do you enforce per-tenant cost budgets for LLM calls?"
- "Prompt injection: what is it, what's the impact, how do you defend?"
- "How do you test an LLM application deterministically?"
- "Your prompt change broke production. How do you detect and roll back?"
- "Compare LLM observability with classic microservice observability — what's new?"
