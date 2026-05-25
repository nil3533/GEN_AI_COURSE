# Module 7 — MCP & Agents in Spring AI

> **Weeks 11–12 · ~16 hours**

---

## What you'll walk away with

A precise understanding of what an "agent" actually is (LLM + loop + tools), and fluency with **MCP (Model Context Protocol)** — the industry standard for LLM ↔ tool integration that Spring AI supports as a first-class citizen. You'll build both:

- An **MCP client** in Spring Boot that consumes tools from external MCP servers
- An **MCP server** in Spring Boot that exposes your beans as tools other LLM clients can use

MCP is the "JDBC for LLM tools" — learning it now puts you ahead of 95% of engineers and aligns with where the industry is heading.

---

## Files (outline)

1. `01_what_is_an_agent_really.md` — Demystifying "agent"; the LLM+loop+tools formula; agents vs chains vs prompts
2. `02_when_agents_shine_and_when_they_dont.md` — Honest trade-offs; cost, latency, reliability of agentic systems
3. `03_simple_agents_in_spring_ai.md` — Building a basic agent using `ChatClient` + `@Tool` beans + a loop
4. `04_mcp_fundamentals.md` — What MCP is, the protocol (JSON-RPC over stdio or HTTP), why Anthropic created it
5. `05_spring_ai_mcp_client.md` — `spring-ai-starter-mcp-client` setup; connecting to MCP servers via stdio or SSE
6. `06_spring_ai_mcp_server.md` — `spring-ai-starter-mcp-server`; exposing `@Tool` beans as MCP tools
7. `07_real_mcp_servers_filesystem_postgres_github.md` — Connecting to Anthropic's reference MCP servers; what's in the ecosystem
8. `08_building_a_custom_mcp_server.md` — Step-by-step: a custom MCP server exposing your existing services
9. `09_mcp_vs_tool_calling_when_to_use_which.md` — Native `@Tool` is great inside your app; MCP wins for cross-app tool sharing
10. `10_safe_agent_design_patterns.md` — Sandboxing, allow-lists, idempotency, human-in-the-loop, max iteration limits
11. `11_real_world_agent_failure_modes.md` — Infinite loops, runaway costs, hallucinated tool calls — and how to prevent

## Mini-project

**"Research agent with MCP tools."** Build an agent in Spring Boot that, given a research question, can:
- Use a `WebSearchTool` (`@Tool`) hitting Tavily or SerpAPI free tier
- Use a `FetchUrlTool` (`@Tool`) to read web pages
- Use the **filesystem MCP server** (Anthropic reference) to write findings to a local file
- Use your **own custom MCP server** that exposes your M5 pgvector knowledge base as a "search company docs" tool

Add: max 10 iterations, max ₹5 cost per request, full Advisor-based logging from M6.

**Bonus:** Build a second tiny Spring Boot app that's *just* an MCP server (exposing Jira-like ticket tools). Have your research agent consume it. Now you've built both sides of the MCP ecosystem.

**Push to:** `sani-genai-journey/m07-mcp-agent/`

## Curated free resources

- Spring AI Reference — "Model Context Protocol (MCP)" section (entire)
- Spring AI examples — `mcp-client-*` and `mcp-server-*` folders on GitHub
- Anthropic MCP documentation — `modelcontextprotocol.io`
- Anthropic reference MCP servers — `github.com/modelcontextprotocol/servers`

## Interview prep highlights

- "What is an agent? How is it different from a chain or a single prompt?"
- "Walk me through what happens on the wire when an LLM calls a tool via MCP."
- "What is MCP? Why is it preferable to provider-specific function calling?"
- "Design tool descriptions for a coding agent. What goes in matters how?"
- "Your agent is in an infinite loop. Debug it."
- "How would you let an agent act on your production Jira safely?"
- "Compare native `@Tool` calling vs exposing the same capabilities via MCP. When to use which?"
- "Spring AI's MCP server exposes my Spring beans as tools. How do I make sure only authenticated callers can use them?"
