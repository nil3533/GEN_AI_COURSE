# Designing Good Tools: Descriptions Matter

> Module 4 - File 7 of 10 - Tool metadata is part of correctness

## Why This Matters

The model chooses tools from names, descriptions, and argument schemas. Poor descriptions create poor routing.

## Concept in Plain English

A good tool description answers:

- what the tool does
- when to use it
- what arguments mean
- what not to use it for

## Good vs Weak

Weak:

```java
@Tool(name = "data", description = "Gets data")
```

Better:

```java
@Tool(
    name = "getOrderStatus",
    description = "Get current fulfillment and delivery status for exactly one order id."
)
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Generic names | Tool confusion |
| Overlapping tools | Random selection |
| Hidden side effects | Unsafe actions |
| Ambiguous arguments | Hallucinated values |

## When To Use

Write tool descriptions as carefully as API documentation. They are read by the model at decision time.

## Production Considerations

Include negative guidance for dangerous tools, such as "Do not call this tool unless the user explicitly confirmed cancellation."

## Interview Questions

1. Why can a tool description change runtime behavior?
2. How would you debug wrong tool selection?
3. Why should tool results be small?

## Further Reading

- Spring AI Tools
- Prompt design evaluation
