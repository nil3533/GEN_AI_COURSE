# Validation with Jakarta Validation

> Module 4 - File 4 of 10 - Parsed does not mean trusted

## Why This Matters

The model can produce valid JSON that is still wrong. Validation is where you enforce your application contract.

## Concept in Plain English

Parsing answers this question:

```text
Is it JSON that fits the Java shape?
```

Validation answers this:

```text
Are the values acceptable for this application?
```

## Code Example

```java
public record AssistantRequest(
        @NotBlank String customerId,
        @NotBlank @Size(max = 2000) String message,
        boolean confirmed
) {
}
```

For output, validate manually after model conversion:

```java
Set<ConstraintViolation<AssistantResponse>> violations = validator.validate(response);
if (!violations.isEmpty()) {
    throw new IllegalStateException("Model returned invalid response");
}
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Validating request only | Model output can still be bad |
| Retrying forever | Tool loops and cost spikes |
| Returning raw parse errors to users | Leaks internals |
| Ignoring invalid enum-like text | Wrong business action |

## When To Use

Always validate API requests. Validate model output when it drives application behavior, persistence, billing, permissions, or customer-visible actions.

## Production Considerations

Use one retry at most for invalid model JSON. If it still fails, return a controlled error or ask the user to rephrase.

## Interview Questions

1. Why is model output untrusted?
2. How many retries are reasonable?
3. What should be logged on validation failure?

## Further Reading

- Jakarta Validation
- Spring Boot validation
