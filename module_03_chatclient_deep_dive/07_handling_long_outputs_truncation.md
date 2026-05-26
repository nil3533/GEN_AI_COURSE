# Handling Long Outputs and Truncation

> Module 3 - File 7 of 7 - When the model stops before the user is done

## Why This Matters

LLMs have output limits. A response can stop because it is complete, because it hit `maxTokens`, because the provider timed out, or because a proxy closed the connection.

## Concept in Plain English

Long-output handling means designing for partial answers:

1. Set a reasonable output limit.
2. Ask for structured sections.
3. Detect whether the answer looks incomplete.
4. Offer a continuation path.

## Deeper Mechanics

Some providers expose a finish reason in metadata. When metadata is unavailable, use application-level guardrails:

- ask the model to end with a marker such as `END_OF_ANSWER`
- split large tasks into sections
- stream progress so the user sees work as it happens
- continue from a known section instead of asking "continue" blindly

## Code Example

```java
String prompt = """
        Write the answer in these sections:
        1. Summary
        2. Steps
        3. Common mistakes

        End with: END_OF_ANSWER
        """;
```

After the response:

```java
boolean looksComplete = answer.endsWith("END_OF_ANSWER");
```

Do not show control markers to users. Strip them after validation.

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Asking for a huge answer in one turn | Truncation or timeout |
| Blindly sending "continue" | Repetition or topic drift |
| No section plan | Hard to resume cleanly |
| No max token cap | Cost spikes |

## When To Use

Use continuation flows for reports, migration plans, summaries of large files, and any endpoint that can naturally be divided into sections.

## Production Considerations

For large generation tasks, prefer async jobs with persisted state. Streaming helps the user experience, but it is not a durable workflow engine. Module 8 covers stateful workflows.

## Interview Questions

1. How can you tell a response was truncated?
2. Why is "continue" a weak continuation strategy?
3. When should long generation become an async job?

## Further Reading

- Module 8 stateful workflows
- Module 9 observability and prompt versioning
