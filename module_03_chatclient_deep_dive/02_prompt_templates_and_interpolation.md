# Prompt Templates and Interpolation

> Module 3 - File 2 of 7 - Runtime variables without string-spaghetti prompts

## Why This Matters

Hard-coded prompts do not survive real products. You need the same prompt shape to accept runtime values like persona, date, product, locale, customer tier, output format, and retrieved context.

## Concept in Plain English

A prompt template is a prompt with named slots.

Example:

```text
You are {persona}.
Today is {currentDate}.
Answer for a Java Spring Boot engineer.
```

At runtime, the application fills `persona` and `currentDate`.

## Deeper Mechanics

Templates help you keep the prompt stable while changing only the inputs. That matters because prompts should be treated like code: reviewed, versioned, tested, and measured.

Avoid building prompts through uncontrolled string concatenation. It becomes hard to audit and easy to break when user text contains braces, quotes, or Markdown.

## Code Example

```java
String answer = chatClient.prompt()
        .system(system -> system
                .text("""
                        You are {persona}.
                        Today is {currentDate}.
                        Answer with practical Java and Spring Boot examples.
                        """)
                .param("persona", "a senior backend mentor")
                .param("currentDate", LocalDate.now().toString()))
        .user("Explain Server-Sent Events for LLM streaming.")
        .call()
        .content();
```

## Common Pitfalls

| Pitfall | Why it hurts | Better approach |
|---|---|---|
| Concatenating long strings | Hard to review and test | Template text plus named params |
| Putting user text inside system template params | User can blur instruction boundaries | Keep user content in user messages |
| Changing prompts silently | Behavior shifts without traceability | Add a prompt version |
| Over-templating everything | Prompts become unreadable | Template only real variables |

## When To Use

Use templates when the same instruction needs runtime values. Do not use templates just to avoid writing normal Java variables.

## Production Considerations

Keep prompt templates close to the feature that owns them. For larger systems, move stable prompts into versioned resource files and test their rendering.

## Interview Questions

1. What problem does prompt templating solve?
2. Why should user input usually stay in a user message?
3. How would you test prompt rendering?

## Further Reading

- Spring AI Prompt Templates
- Spring AI ChatClient system prompt parameters
