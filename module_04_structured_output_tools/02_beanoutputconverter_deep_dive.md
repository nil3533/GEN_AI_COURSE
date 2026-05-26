# BeanOutputConverter Deep Dive

> Module 4 - File 2 of 10 - Schema generation and parsing

## Why This Matters

`BeanOutputConverter` is the Spring AI bridge between Java types and model JSON. It generates format instructions from your class and converts the model response back into that type.

## Concept in Plain English

You give Spring AI a Java type. Spring AI creates schema-style instructions. The model follows those instructions. Spring AI parses the answer.

## Deeper Mechanics

`BeanOutputConverter` does two useful things:

- `getFormat()` gives prompt instructions for the expected JSON shape.
- `convert(text)` parses model text into the target type.

`ChatClient.call().entity(MyType.class)` uses the same idea in a more compact form.

## Code Example

```java
var converter = new BeanOutputConverter<>(AssistantResponse.class);

String raw = chatClient.prompt()
        .system("""
                You are an order assistant.
                Return your answer using this format:
                {format}
                """)
        .user(userMessage)
        .call()
        .content();

AssistantResponse response = converter.convert(raw);
```

With `ChatClient` shorthand:

```java
AssistantResponse response = chatClient.prompt()
        .system("Return a valid AssistantResponse JSON object.")
        .user(userMessage)
        .call()
        .entity(AssistantResponse.class);
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Not including format instructions | Model returns prose |
| Fields with vague names | Model fills them inconsistently |
| No failure path | Invalid JSON becomes a 500 |
| Schema too large | Model ignores details |

## When To Use

Use `entity(Class)` for straightforward typed output. Use an explicit converter when you want to inspect or include `getFormat()` yourself.

## Production Considerations

Log parse failures with prompt version and model, but do not log sensitive customer text unless approved.

## Interview Questions

1. What does `BeanOutputConverter.getFormat()` produce?
2. What should your service do when conversion fails?
3. Why can a valid JSON object still be wrong?

## Further Reading

- Spring AI `BeanOutputConverter`
- Jackson record binding
