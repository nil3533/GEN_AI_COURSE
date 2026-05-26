# GitHub Launch Checklist

Use this before promoting the repository publicly.

## Repository Basics

- [ ] Add a clear repository description:

```text
Java/Spring Boot-first GenAI course with runnable Spring AI mini-projects: ChatClient, streaming, tool calling, RAG, pgvector, agents, and production patterns.
```

- [ ] Add repository topics:

```text
spring-ai, spring-boot, java, generative-ai, llm, rag, pgvector, ollama, groq, tool-calling, ai-agents, chatclient
```

- [ ] Keep license omitted for now, by project-owner decision.
- [ ] Pin the repository on your GitHub profile.
- [ ] Add a short profile README link to this repo.

## Social Preview

- [x] Export `docs/assets/social-preview.svg` as `docs/assets/social-preview.png`.
- [ ] Upload `docs/assets/social-preview.png` in GitHub repository settings under Social preview.
- [ ] Share the repository link once and verify the preview looks clean.

## First Public Release

- [ ] Create a tag:

```powershell
git tag v0.1.0
git push origin v0.1.0
```

- [ ] Create a GitHub release:

```text
v0.1.0 - Spring AI foundations, streaming, and tool calling
```

- [ ] Mention completed modules:
  - Module 1: raw LLM HTTP call
  - Module 2: Spring AI ChatClient and provider switching
  - Module 3: SSE streaming chat API
  - Module 4: structured output and tool calling

## README Quality Check

- [ ] First screen explains who the repo is for.
- [ ] Quickstart runs a completed mini-project.
- [ ] Module table shows status.
- [ ] Each completed mini-project has:
  - [ ] `mvn test`
  - [ ] run command
  - [ ] curl examples
  - [ ] expected response shape
  - [ ] architecture or walkthrough notes

## Promotion Copy

Short post:

```text
I am building a Java/Spring Boot-first GenAI course with runnable Spring AI projects: raw LLM calls, ChatClient, SSE streaming, tool calling, RAG with pgvector, agents, and production patterns. No Python required.
```

Longer hook:

```text
Most GenAI tutorials assume Python. This repo is for Java/Spring Boot engineers who want to build production-style AI apps with Spring AI, Groq, Ollama, pgvector, tool calling, streaming, observability, and a capstone.
```

## What To Build Next

The next attention-grabbing milestone is Module 5:

```text
RAG with PostgreSQL + pgvector: chat with your own docs using Spring AI.
```

That module is likely to attract more stars because RAG is a common real-world use case.
