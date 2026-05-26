# Structured Output for Classification and Extraction

> Module 4 - File 3 of 10 - Common typed-output patterns

## Why This Matters

Most backend LLM features are not open-ended chat. They classify, extract, route, summarize, or normalize data. Structured output makes those workflows testable.

## Concept in Plain English

Common patterns:

| Pattern | Example |
|---|---|
| Classification | `intent = ORDER_STATUS` |
| Extraction | `orderId = ORD-1001` |
| List extraction | `lineItems = [...]` |
| Nested object | `shippingAddress = {...}` |
| Decision output | `actionRequired = true` |

## Code Example

```java
public enum AssistantIntent {
    ORDER_STATUS,
    RECENT_ORDERS,
    CANCEL_ORDER,
    GENERAL_HELP,
    UNKNOWN
}
```

```java
public record AssistantResponse(
        AssistantIntent intent,
        String answer,
        boolean actionRequired,
        double confidence
) {
}
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Using free-form strings for everything | Hard to branch safely |
| Too many enum values | Low classification accuracy |
| Missing `UNKNOWN` | Forced wrong decisions |
| No confidence field | Hard to route low-confidence cases |

## When To Use

Use enums for routing decisions. Use nested records when data has clear structure. Keep schemas small until tests prove you need more detail.

## Production Considerations

Route low-confidence responses to fallback logic, clarification, or manual review instead of forcing an automated action.

## Interview Questions

1. Why include an `UNKNOWN` enum value?
2. What belongs in structured metadata versus user-facing answer text?
3. How would you test extraction reliability?

## Further Reading

- Java records
- Jakarta Validation
