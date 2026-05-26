# Pitfalls: Loops, Costs, and Hallucinated Arguments

> Module 4 - File 10 of 10 - Guardrails for real systems

## Why This Matters

Tool calling gives the model access to capabilities. That makes guardrails mandatory.

## Common Failure Modes

| Failure mode | Example | Guard |
|---|---|---|
| Tool loop | Calls status tool repeatedly | Max tool calls per request |
| Hallucinated argument | Uses order `123` when ids are `ORD-1001` | Validate ids |
| Unsafe mutation | Cancels without confirmation | Confirmation flag |
| Cost spike | Broad request triggers many calls | Narrow tools and budget |
| Data leak | Customer asks for another user's order | Authorization check |

## Code Example

```java
if (!context.cancellationConfirmed()) {
    return new CancelOrderResult(orderId, "REJECTED", "Cancellation requires explicit confirmation.");
}
```

Tool budget:

```java
if (state.incrementAndGet() > maxToolCalls) {
    throw new ToolBudgetExceededException("Tool call budget exceeded");
}
```

## When To Use

Use guardrails for every tool. Read-only tools still need authorization and argument validation.

## Production Considerations

Never let the model bypass normal business rules. Tool calling is another client of your service layer, not an admin path.

## Interview Questions

1. What is a tool-call loop?
2. How do you stop hallucinated arguments from causing damage?
3. Why are write tools more dangerous than read tools?

## Further Reading

- Module 9 observability
- Module 13 capstone safety design
