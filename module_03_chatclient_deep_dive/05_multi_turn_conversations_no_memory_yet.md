# Multi-Turn Conversations: No Memory Yet

> Module 3 - File 5 of 7 - Manual history before Spring AI memory

## Why This Matters

Users expect chat to remember what they just said. But durable memory is a bigger design problem involving storage, retention, privacy, summarization, and retrieval. Module 3 keeps it simple: short in-memory history per session.

## Concept in Plain English

For each `sessionId`, store recent user and assistant messages:

```text
sessionId -> [UserMessage, AssistantMessage, UserMessage, AssistantMessage]
```

On the next request, replay that list before the new user message.

## Deeper Mechanics

Manual history is context replay, not true memory. The model does not remember anything by itself. Every request must include the relevant previous messages.

Keep history bounded. Otherwise cost, latency, and context-window pressure grow with every turn.

## Code Example

```java
private final Map<String, List<Message>> sessions = new ConcurrentHashMap<>();

List<Message> history = sessions.computeIfAbsent(sessionId, ignored ->
        Collections.synchronizedList(new ArrayList<>()));

List<Message> historySnapshot;
synchronized (history) {
    historySnapshot = history.stream()
            .skip(Math.max(0, history.size() - 12))
            .toList();
}
```

After a successful response:

```java
synchronized (history) {
    history.add(new UserMessage(userText));
    history.add(new AssistantMessage(answer));
}
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Storing unbounded history | Slow and expensive calls |
| Appending partial streamed answers | Corrupt future context |
| Sharing history across users | Privacy breach |
| Treating in-memory history as durable | Lost conversations on restart |

## When To Use

Use manual in-memory history for demos, local learning, and small prototypes. Use durable memory, storage policies, and summarization for production. That comes in Module 6.

## Production Considerations

Attach history to an authenticated user or tenant, not only a browser-provided string. Add expiration and clear-session endpoints.

## Interview Questions

1. Why is manual history not true memory?
2. How do you stop history from growing forever?
3. What privacy bug can happen with session IDs?

## Further Reading

- Spring AI Chat Memory, covered in Module 6
- Spring Session for real user sessions
