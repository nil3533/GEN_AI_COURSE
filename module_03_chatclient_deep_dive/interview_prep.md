# Module 3 Interview Prep

## Short Answers

### Why use streaming for LLM responses?

Streaming reduces perceived latency. The total model call may still take the same time, but the user sees useful text as soon as the provider emits tokens.

### Why SSE instead of WebSockets?

SSE is simpler for one-way server-to-browser streams. LLM token streaming usually needs request once, receive many events. WebSockets are better when both sides need continuous bidirectional messages.

### What is manual chat history?

The server stores prior user and assistant messages and sends a bounded subset with the next request. The model does not remember the session unless the application includes that history again.

### What should happen on client disconnect?

Log the cancellation, avoid appending partial assistant output to history, and let the provider call stop if the underlying client supports cancellation.

### What does temperature control?

Temperature controls randomness. Lower values are better for factual, repeatable answers. Higher values can help brainstorming but increase variability.

## Debugging Scenarios

### Streaming endpoint returns everything at once

Check the client first: use `curl --no-buffer`. Then check reverse proxy buffering, response content type, and whether the service is returning a `Flux` instead of collecting the answer.

### Follow-up question loses context

Verify the same `sessionId` is sent, history is appended only after success, and the service replays history before the current user message.

### Responses become slow after many turns

Check history size and token usage. Bound the number of replayed messages or summarize older turns.

### Model ignores output instructions

Move durable rules to the system message, remove conflicting examples, lower temperature, and test with a small eval set.

## Practice Prompts

1. Explain `ChatClient.prompt().stream().content()` to a backend engineer.
2. Design a streaming endpoint that can survive browser disconnects.
3. Compare manual history, Spring AI memory, and RAG.
4. Explain why prompt templates should be versioned.
