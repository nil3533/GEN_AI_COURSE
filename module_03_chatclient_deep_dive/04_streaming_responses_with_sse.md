# Streaming Responses with SSE

> Module 3 - File 4 of 7 - Make model latency feel usable

## Why This Matters

LLM calls often take seconds. A non-streaming endpoint makes the user stare at a spinner until the full answer is ready. Streaming sends partial text as soon as the model produces it.

## Concept in Plain English

Server-Sent Events are a one-way HTTP stream from server to browser. They are simpler than WebSockets when the browser only needs to receive tokens.

Typical event flow:

```text
start -> token -> token -> token -> done
```

## Deeper Mechanics

In Spring, a streaming endpoint can return `Flux<ServerSentEvent<T>>` with `produces = text/event-stream`.

The LLM gateway returns a `Flux<String>` of token chunks. The controller wraps each chunk as an SSE event.

## Code Example

```java
@PostMapping(value = "/api/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<ChatStreamEvent>> stream(@Valid @RequestBody ChatRequest request) {
    return chatConversationService.stream(request);
}
```

Client test:

```powershell
curl.exe --no-buffer -X POST http://localhost:8081/api/chat/stream `
  -H "Content-Type: application/json" `
  -d "{\"message\":\"Explain SSE in 3 bullets\"}"
```

## Common Pitfalls

| Pitfall | Result | Fix |
|---|---|---|
| Buffering in client or proxy | Tokens arrive all at once | Use `curl --no-buffer`; configure proxy buffering |
| No disconnect handling | Wasted provider calls | Log cancellation and avoid appending partial history |
| No error event | Browser sees silent close | Emit an `error` SSE event |
| Too many tiny events | UI overhead | Append chunks efficiently on the client |

## When To Use

Use SSE for LLM response streaming, progress events, and long-running read-only workflows. Use WebSockets only when the client must send many messages over the same open connection.

## Production Considerations

Set sensible gateway and proxy timeouts. Test behind the same reverse proxy you will deploy with because proxy buffering can break streaming behavior.

If a provider path uses a blocking HTTP client internally, do not execute it on a Reactor event-loop thread such as `reactor-http-nio-*`. Wrap blocking calls with `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())`, or subscribe the streaming `Flux` on `boundedElastic`.

## Interview Questions

1. Why is SSE often better than WebSockets for LLM streaming?
2. How do you test whether streaming is actually streaming?
3. What should your service do when the client disconnects?

## Further Reading

- Spring WebFlux SSE
- Spring AI streaming ChatClient calls
