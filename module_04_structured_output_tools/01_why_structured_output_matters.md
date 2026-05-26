# Why Structured Output Matters

> Module 4 - File 1 of 10 - Stop parsing prose when your code needs data

## Why This Matters

LLM text is useful for humans, but code needs stable fields. If your service needs `intent`, `orderId`, `confidence`, or `requiresConfirmation`, a loose paragraph is a weak contract.

Structured output turns model output into typed Java records that your controller, service, and tests can reason about.

## Concept in Plain English

Instead of asking:

```text
Tell me what the user wants.
```

Ask for a schema:

```json
{
  "intent": "ORDER_STATUS",
  "orderId": "ORD-1001",
  "confidence": 0.91
}
```

Then parse it into a Java record.

## Deeper Mechanics

The model still generates text. The difference is that your prompt gives it a JSON contract and your application rejects malformed responses instead of guessing.

## Code Example

```java
public record IntentResult(
        String intent,
        String orderId,
        double confidence
) {
}
```

```java
var result = chatClient.prompt()
        .system("Classify the customer request.")
        .user(userMessage)
        .call()
        .entity(IntentResult.class);
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Regex over natural language | Breaks when wording changes |
| No enum or field constraints | Downstream code gets surprises |
| Treating model JSON as trusted | Security and data-quality bugs |
| Huge nested schemas too early | More invalid responses |

## When To Use

Use structured output when software consumes the response. Use plain text when the response is only displayed to a human.

## Production Considerations

Validate the parsed object. A well-formed JSON response can still contain business-invalid values.

## Interview Questions

1. Why is typed output better than parsing prose?
2. What still needs validation after JSON parsing?
3. When is plain text enough?

## Further Reading

- Spring AI Structured Output Converters
- Module 9 prompt versioning
