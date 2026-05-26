# Repository Guidelines

## Project Structure & Module Organization

This repository is a Spring AI self-study course, organized as Markdown-first learning material. Root files provide shared context: `00_MASTER_ROADMAP.md`, `01_GLOSSARY.md`, and `02_ENVIRONMENT_SETUP.md`. Module folders use `module_XX_topic/`, for example `module_05_rag_with_pgvector/`.

Place module-specific notes, exercises, assets, and screenshots inside the matching folder. Runnable Spring Boot work should follow the module README, usually under a project subfolder such as `module_01_foundations/mini_project/`, with Maven layout: `src/main/java/`, `src/test/java/`, and `src/main/resources/`. Put future capstone ADRs in `docs/adr/`.

## Build, Test, and Development Commands

The root currently has no unified build. Use these commands inside module projects:

- `./mvnw spring-boot:run`: start a Spring Boot service locally.
- `./mvnw test`: run unit and integration tests for a Maven project.
- `docker compose up -d`: start local PostgreSQL/pgvector or Ollama services from `docker-compose.yml`.
- `npm install` and `npm run dev`: install and run React/Vite work in Module 10 projects.
- `npm run build`: verify a React frontend production build.

For documentation-only changes, preview Markdown and verify links and code fences manually.

## Coding Style & Naming Conventions

Keep prose concise, instructional, and consistent with existing module READMEs. Use `module_XX_descriptive_slug` for new module directories and lower snake case for notes unless a module establishes another pattern.

For Java projects, target Java 21, Spring Boot 3.5.x, Maven, and Spring AI. Keep controllers, services, and configuration separated. Read provider credentials from environment variables such as `GROQ_API_KEY`. Avoid adding Python examples or tooling unless explicitly required for comparison.

## Testing Guidelines

Add tests beside code in `src/test/java/`. Prefer JUnit, Spring Boot test utilities, MockRestServiceServer for external LLM calls, and Testcontainers for PostgreSQL/pgvector integration tests. Name tests after behavior, for example `returnsAnswerWhenProviderResponds`.

Do not call paid or flaky live LLM APIs from default tests. Use mocks, local Ollama, or documented opt-in profiles.

## Commit & Pull Request Guidelines

Recent history uses imperative commit subjects, for example `Add modules for structured output tools...`. Continue with subjects such as `Add module 05 RAG exercises` or `Document Ollama setup`. Keep commits focused by module or concern.

Pull requests should include a summary, changed module paths, validation performed, and screenshots for frontend work. Link related issues or course tasks when available.

## Security & Configuration Tips

Never commit API keys, `.env` files with secrets, provider tokens, or generated credentials. Document required variables with placeholders. Prefer local Docker services for repeatable exercises, and mark paid-provider usage as optional.
