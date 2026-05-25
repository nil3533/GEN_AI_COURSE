# Module 4 — Structured Output & Tool Calling (Function Calling)

> **Weeks 6–7 · ~14 hours**

---

## What you'll walk away with

Two of the most production-critical Spring AI skills:

1. **Structured Output** — getting the LLM to return strongly-typed Java records, not strings. Spring AI's `BeanOutputConverter` generates a JSON schema from your class, injects it into the prompt, and parses the reply. The difference between this and "parse the string yourself with regex" is the difference between toy and production.

2. **Tool Calling (Function Calling)** — letting the LLM invoke your existing Spring beans. Annotate a method with `@Tool`, register it on the `ChatClient`, and the LLM decides when to call it. The LLM becomes a router into your existing business logic.

These two skills convert your Spring Boot service from "LLM chatbot wrapper" to "LLM-driven orchestrator of your services."

---

## Files (outline)

1. `01_why_structured_output_matters.md` — The shift from "string parsing" to "typed records"; the production reliability story
2. `02_beanoutputconverter_deep_dive.md` — How it works internally; schema generation; what happens when the LLM produces invalid JSON
3. `03_structured_output_for_classification_and_extraction.md` — Patterns: enums, lists of records, nested structures
4. `04_validation_with_jakarta_validation.md` — Combining `BeanOutputConverter` with `@Valid`, retries on validation failure
5. `05_introduction_to_tool_calling.md` — The protocol: LLM proposes a call, your code executes, you feed the result back
6. `06_the_tool_annotation_in_spring_ai.md` — `@Tool` on a Spring bean method, `@ToolParam` for argument descriptions
7. `07_designing_good_tools_descriptions_matter.md` — Tool descriptions are read by the LLM — they're as important as the code
8. `08_multi_tool_assistants.md` — Registering many tools; how the LLM picks; safe defaults
9. `09_tool_calling_vs_json_mode_vs_response_format.md` — When each is appropriate; provider-specific support matrix
10. `10_pitfalls_loops_costs_hallucinated_args.md` — Real failure modes; how to guard

## Mini-project

**"Order assistant with real backend tools."** Build a Spring Boot service that:
- Exposes `POST /api/assistant`
- Has a real `OrderService` with methods `getOrderStatus(orderId)`, `getRecentOrders(customerId)`, `cancelOrder(orderId, reason)` — backed by an in-memory store or H2
- Registers those methods as `@Tool` beans on a `ChatClient`
- The LLM autonomously decides which tool to call based on the user question
- Returns a strongly-typed `AssistantResponse` record (using `BeanOutputConverter`) containing the answer plus structured metadata: which tools were called, with what arguments, and what they returned

Add: a budget guard limiting tool calls to 5 per request. A safety check rejecting `cancelOrder` unless the user has explicitly confirmed.

**Push to:** `sani-genai-journey/m04-tool-calling-assistant/`

## Curated free resources

- Spring AI Reference — "Structured Output Converters" and "Tools" sections
- Spring AI examples GitHub — `tool-calling/` folder
- Anthropic Cookbook — "Tool Use" recipes (`github.com/anthropics/anthropic-cookbook`)
- Dan Vega — Spring AI tool calling YouTube videos

## Interview prep highlights

- "How does `BeanOutputConverter` actually work? Walk me through what gets sent to the LLM."
- "Walk me through the wire-level protocol when an LLM calls a tool. What does the LLM send, what does the API send back, what does the LLM send next?"
- "How would you let an LLM modify orders in a banking app safely?"
- "Your tool descriptions all use generic words like `data` and `info`. Why does this matter, and how do you fix it?"
- "Design tool calling for an agent that should never call the same tool twice in a row."
- "Your agent is making 50 tool calls per user question. What knobs do you turn?"
