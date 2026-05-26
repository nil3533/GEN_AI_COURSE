# Prompt Engineering Techniques in Spring AI

> Module 3 - File 3 of 7 - Practical prompting patterns for backend services

## Why This Matters

Prompt engineering is not magic wording. It is API design for a probabilistic dependency. A good prompt narrows the task, gives examples when needed, and makes the expected output easy to validate.

## Concept in Plain English

| Pattern | Use when | Example |
|---|---|---|
| Zero-shot | The task is simple | "Summarize this support ticket in 3 bullets." |
| Few-shot | The model needs examples | Show 2 input-output pairs before the real input |
| Role prompting | Tone or expertise matters | "Act as a senior Spring Boot reviewer." |
| Structured instruction | Output must be consumed by code | "Return JSON with these fields..." |
| Reasoning prompt | The task needs planning | "List assumptions, then give the answer." |

## Deeper Mechanics

Few-shot prompting costs more tokens, but it can reduce ambiguity. Use it when output shape, tone, or classification boundaries are hard to describe with rules alone.

Avoid asking the model to reveal hidden chain-of-thought. In production, ask for concise reasoning summaries or decision factors instead.

## Code Example

```java
String answer = chatClient.prompt()
        .system("""
                You classify support tickets for a Spring Boot SaaS team.
                Return only one label: BUG, BILLING, SECURITY, or HOW_TO.
                """)
        .user("""
                Examples:
                Input: The invoice total is wrong.
                Label: BILLING

                Input: I get 403 after login.
                Label: SECURITY

                Input: {ticket}
                Label:
                """)
        .call()
        .content();
```

In a real app, use template params for `{ticket}` instead of manual replacement.

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Asking for too many things in one prompt | Lower reliability |
| Using examples that conflict with rules | Inconsistent output |
| Making hidden reasoning part of the contract | Hard to test and audit |
| Trusting structured output without parsing | Runtime surprises |

## When To Use

Start with zero-shot. Add examples only when tests show the model is inconsistent. Move to structured output in Module 4 when code must consume the response.

## Production Considerations

Keep a small eval set for prompt changes. For this module, five saved test questions are enough. Later, Module 9 turns this into proper observability and prompt versioning.

## Interview Questions

1. When would you choose few-shot over zero-shot?
2. Why should examples be short and representative?
3. How do you make a prompt change safer before release?

## Further Reading

- Anthropic prompt engineering guide
- Spring AI structured output, covered in Module 4
