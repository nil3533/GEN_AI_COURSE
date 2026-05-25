# Module 10 — React Frontends for Your AI APIs

> **Week 16 · ~10 hours**

> **Minimal, pragmatic React.** You said you have basic React skills. The goal here isn't to make you a frontend engineer — it's to give you just enough React to build polished UIs around your Spring Boot AI services. No deep React state management lectures; just patterns you can copy-adapt.

---

## What you'll walk away with

The ability to build a clean React UI that consumes your streaming Spring Boot endpoints — specifically, a ChatGPT-like chat interface that handles SSE streaming, conversation history, loading states, errors, and rendering markdown responses. You'll also build a RAG-flavored UI showing source citations as expandable cards.

By the end, your portfolio's screenshots look professional, not like 1998 enterprise software.

---

## Files (outline)

1. `01_minimal_react_setup_with_vite.md` — Vite + React 19 + Tailwind in 5 minutes; no Create React App
2. `02_calling_spring_boot_from_react.md` — `fetch`, CORS config in Spring Boot, environment variables for the backend URL
3. `03_consuming_sse_streams_in_react.md` — The native `EventSource` API; reading from `fetch` with `ReadableStream`; the cancellation gotcha
4. `04_a_clean_chat_ui_component.md` — Message list, input box, auto-scroll, loading dots; ~150 lines of code
5. `05_streaming_tokens_to_the_ui.md` — Appending tokens as they arrive; making it feel like ChatGPT
6. `06_markdown_rendering_with_react_markdown.md` — Code blocks, lists, links — making LLM responses look native
7. `07_displaying_citations_for_rag.md` — Expandable source cards under each RAG answer
8. `08_handling_long_running_workflows.md` — Showing step-by-step progress for the M8 workflow service
9. `09_tools_called_inline.md` — Showing "calling getOrderStatus(...)" cards inside the chat for tool-calling apps
10. `10_deployment_options.md` — Hosting React frontend on Cloudflare Pages / Vercel (free); reverse-proxying via your Spring Boot or running them separately

## Mini-project

**Unified React UI for your portfolio.** Build a single React app with three tabs:

1. **Chat** — connects to M3's streaming endpoint, with conversation history (M6 backend)
2. **Docs Q&A** — connects to M5's RAG endpoint, shows answer with expandable citations
3. **Research Workflow** — connects to M8's workflow endpoint, shows step-by-step progress

Deploy to Cloudflare Pages or Vercel (free). Add the deployed URL to every project's README. This is the UI recruiters and interviewers see when they click your demo.

**Push to:** `sani-genai-journey/m10-react-frontend/`

## Curated free resources

- Vite docs — `vitejs.dev/guide/`
- React docs — `react.dev/learn` (the new, much better docs)
- Tailwind CSS docs — `tailwindcss.com/docs`
- `react-markdown` docs — for rendering LLM output
- `lucide-react` icons — clean free icon set

## Interview prep highlights

- "How does SSE work? Why prefer it over WebSockets for LLM streaming?"
- "How would you handle a slow LLM response in the UI — what loading states?"
- "Your React app loses the SSE connection mid-stream. How does it recover?"
- "Walk me through CORS configuration for a React-to-Spring-Boot call."
- "How would you render LLM responses that contain code blocks and markdown safely?"
