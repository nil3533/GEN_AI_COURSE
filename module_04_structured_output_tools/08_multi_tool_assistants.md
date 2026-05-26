# Multi-Tool Assistants

> Module 4 - File 8 of 10 - Routing across several backend capabilities

## Why This Matters

Real assistants need more than one capability. An order assistant may check status, list recent orders, cancel an order, and explain policy.

## Concept in Plain English

The model receives a menu of tools. It chooses the tool that best answers the user request.

## Code Example

```java
chatClient.prompt()
        .system("Use order tools only when the user asks about orders.")
        .toolCallbacks(orderToolProvider)
        .user(request.message())
        .call()
        .entity(AssistantResponse.class);
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Too many tools at once | Lower routing accuracy |
| No trace of calls | Impossible to debug |
| Unsafe tools mixed with read tools | Higher risk |
| No budget | Tool-call loops |

## When To Use

Use multi-tool assistants when the feature naturally spans a small set of related capabilities. Do not register the whole backend.

## Production Considerations

Trace every call: tool name, arguments, result summary, and whether it mutated state.

## Interview Questions

1. How many tools should one assistant see?
2. How do you debug an incorrect tool call?
3. What should be included in a tool trace?

## Further Reading

- Module 7 agents
- Module 9 observability
