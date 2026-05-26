# Anatomy of a Prompt

> Module 3 - File 1 of 7 - What the model actually receives

## Why This Matters

Most production bugs in chat applications are not model bugs. They are prompt assembly bugs: the wrong instruction is placed last, previous context is replayed in the wrong order, or user input is allowed to override a system rule.

Spring AI gives you message objects and `ChatClient` builders so prompt construction stays explicit.

## Concept in Plain English

A chat prompt is a list of messages. The model reads them in order and predicts the next assistant message.

Common roles:

| Role | Purpose |
|---|---|
| System | Defines durable behavior: persona, constraints, output format, safety rules |
| User | The human request |
| Assistant | Prior model responses used for conversation continuity |
| Tool response | Data returned by a tool call, covered later in Module 4 |

## Deeper Mechanics

For a normal chat turn, assemble messages in this order:

1. System instruction
2. Previous user and assistant messages
3. Current user message

The last user message should be the current task. Do not bury the real task inside older history.

## Code Example

```java
String answer = chatClient.prompt()
        .system("You are a concise Java and Spring Boot mentor.")
        .user("Explain ChatClient in 3 bullets.")
        .call()
        .content();
```

With prior history:

```java
var history = List.of(
        new UserMessage("What is Spring AI?"),
        new AssistantMessage("Spring AI is a Spring-friendly abstraction for AI models.")
);

String answer = chatClient.prompt()
        .system("You are a concise Java and Spring Boot mentor.")
        .messages(history)
        .user("How is ChatClient different from raw HTTP?")
        .call()
        .content();
```

## Common Pitfalls

| Pitfall | Result | Fix |
|---|---|---|
| Putting rules in the user message only | Easy to override | Put durable rules in system |
| Replaying too much history | Cost and latency grow | Keep a bounded window |
| Reordering history | Confusing answers | Preserve user-assistant order |
| Trusting model output blindly | Security and data bugs | Validate output in application code |

## When To Use

Use explicit message construction for every endpoint that has rules, history, or output requirements. Simple one-off demos can use `.user(...)` only, but production chat flows should define the system message deliberately.

## Production Considerations

Log the prompt version, model, provider, latency, and token counts. Do not log secrets or sensitive user content unless you have a clear retention policy.

## Interview Questions

1. Why does system prompt placement matter?
2. What changes when you add assistant messages to history?
3. Why should prompt rules not replace server-side validation?

## Further Reading

- Spring AI ChatClient API
- Spring AI Prompt API
