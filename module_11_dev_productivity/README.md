# Module 11 — AI-Assisted Development for Java/Spring Engineers

> **Week 17 · ~10 hours**

> **A productivity module, not a building module.** You already use Claude Code and GitHub Copilot. This formalizes the patterns and adds Cursor and Windsurf to your toolkit — specifically for Java/Spring work.

---

## What you'll walk away with

Mastery of the AI-assisted development workflow tuned for Java/Spring engineers: when each tool wins, prompt patterns that work specifically for Spring Boot codebases (where context is heavy and conventions matter), how to use MCP-enabled tools to give your coding assistant access to your real database / Jira / GitHub, and concrete metrics on how much faster you are.

By the end, you should be 2–3× faster at your day job — and able to give a credible interview talk about "how I use AI tools in my day-to-day."

---

## Files (outline)

1. `01_the_ai_native_development_mindset_for_java_engineers.md` — Spec → plan → execute → review; why Spring conventions help AI assistants
2. `02_claude_code_for_spring_boot_projects.md` — Patterns that work well: "explain this service," "add an endpoint," "refactor this Advisor" (you already use this — formalize)
3. `03_github_copilot_advanced_patterns_for_java.md` — Beyond autocomplete: chat mode, slash commands, instructions files for Spring conventions
4. `04_cursor_for_large_codebase_refactoring.md` — Cursor's strengths on monorepos; the agent mode for multi-file changes
5. `05_windsurf_pair_programming.md` — UX comparison; when to switch
6. `06_running_local_coding_models.md` — Qwen-Coder, DeepSeek-Coder via Ollama; when local beats cloud for code work
7. `07_prompt_patterns_that_work_for_spring_code.md` — "Be the senior Java engineer I'd hire", architecture review prompts, test generation prompts, refactor specs
8. `08_mcp_in_your_daily_workflow.md` — MCP servers connecting your AI assistant to your real Jira, GitHub, Postgres
9. `09_when_to_NOT_use_ai_coding_assistants.md` — Critical thinking: where AI suggestions hurt code quality (security-sensitive code, novel algorithms, anything regulated)
10. `10_measuring_your_own_productivity.md` — A 1-week experiment: track time saved, bugs caught, refactors completed

## Mini-project

**No traditional code project.** Instead:

1. Pick a non-trivial side project, old code, or work TODO you've been postponing.
2. Rebuild/build it using only AI-assisted workflows — Claude Code, Copilot, Cursor (your choice).
3. Track honestly: time spent, time saved (estimated), bugs the AI introduced that you caught, bugs you missed.
4. Write a Medium/Dev.to blog post: "How I used AI tools to build X in Y hours" — concrete and honest, not hype.
5. Share on LinkedIn — this is content marketing for your AI Engineer pivot.

The blog post itself becomes career capital. Recruiters who read it before your interview have a much warmer opinion of you.

## Curated free resources

- Anthropic's Claude Code official tutorial series
- Cursor docs and "Cursor for Java" community videos
- GitHub Copilot for Java best practices (docs.github.com/copilot)
- "Latent Space" podcast episodes on AI coding tools
- Theo (`t3.gg`) and Fireship YouTube channels — frequent AI dev tool comparisons

## Interview prep highlights

- "How do you use AI tools in your day-to-day Java/Spring development?"
- "When does AI coding assistance actively hurt code quality?"
- "Walk me through how you'd refactor a legacy Spring monolith using Claude Code."
- "Compare Cursor, Copilot, Claude Code, Windsurf — which for what?"
- "How would you use MCP to give your coding agent safe access to your production Jira?"
- "Show me an AI-assisted PR you submitted. Walk me through your review process."
