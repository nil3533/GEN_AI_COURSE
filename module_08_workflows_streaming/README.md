# Module 8 — Stateful Workflows & Multi-Step Pipelines

> **Weeks 13–14 · ~14 hours**

---

## What you'll walk away with

The ability to design stateful, multi-step LLM workflows in Spring Boot — the Java equivalent of what Python folks use LangGraph for. You'll combine Spring AI with one (or a combination) of:

- **Plain Spring beans + state machine logic** for simple linear workflows
- **Spring Statemachine** for genuinely state-driven flows
- **Spring Integration / Kafka** for asynchronous, long-running, resumable workflows
- **Spring AI's own `Flux`-based composition** for streaming pipelines

This is where you move past "single LLM call" to building **planner → executor → critic → refiner** patterns and asynchronous agentic systems that run for hours and survive restarts.

---

## Files (outline)

1. `01_when_a_single_chatclient_call_isnt_enough.md` — Multi-step patterns; when to reach for them
2. `02_simple_sequential_workflows_in_java.md` — Method composition; `ChatClient` chaining; passing context manually
3. `03_planner_executor_critic_pattern.md` — The canonical multi-step structure; implementation in Spring
4. `04_state_management_with_records.md` — Immutable state records flowing through steps; the functional approach
5. `05_spring_statemachine_for_branching_workflows.md` — When conditional/parallel/iterative flows justify a state machine
6. `06_async_workflows_with_kafka.md` — Long-running workflows survive restarts via Kafka events (your existing skill applies directly)
7. `07_streaming_intermediate_steps_to_ui.md` — Sending step-by-step progress to a React UI via SSE
8. `08_persistence_and_resumability.md` — Checkpointing workflow state to Postgres; resuming from interruptions
9. `09_human_in_the_loop_pauses.md` — Pausing a workflow for approval; storing pending-state; webhook resumption
10. `10_comparison_with_langgraph_when_to_envy_python.md` — Honest comparison; what Python gets for free that you build in Java; and when that's actually fine

## Mini-project

**"Research workflow with checkpointing."** Build a Spring Boot service exposing `POST /api/workflow/research` that:
- Takes a research question
- Step 1: Planner LLM call breaks question into 3–5 sub-queries
- Step 2: For each sub-query, retrieve from M5 pgvector RAG + web search (M7)
- Step 3: Synthesizer LLM call combines findings
- Step 4: Critic LLM call flags weak sections; if any, loop back to step 2 for those sub-queries (max 2 loops)
- Step 5: Final answer with citations

State is persisted to Postgres after each step (workflow_runs table). If the service is killed mid-run, restarting it resumes from the last completed step. SSE streams step-by-step progress to the client.

**Push to:** `sani-genai-journey/m08-workflow-pipelines/`

## Curated free resources

- Spring Statemachine reference docs
- Spring Integration reference (specifically the messaging patterns)
- LangGraph Python tutorials (read for ideas — don't copy code; implement Java equivalents)
- Anthropic — "Building effective agents" blog post (essential reading; language-agnostic patterns)

## Interview prep highlights

- "Compare Spring AI multi-step workflows with LangGraph. What does Python get that Java doesn't?"
- "Design a multi-agent research system in Spring Boot. Cover state, persistence, resumability."
- "When should you use Spring Statemachine vs Kafka events vs plain method composition?"
- "Your workflow runs for 4 hours and the server restarts. How do you survive?"
- "Add human-in-the-loop approval to a workflow. Walk through the data model and API."
- "Stream multi-step progress to a React UI. What protocol, what data model?"
