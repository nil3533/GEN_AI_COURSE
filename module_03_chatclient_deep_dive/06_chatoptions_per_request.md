# Chat Options Per Request

> Module 3 - File 6 of 7 - Runtime control without code branches

## Why This Matters

Different tasks need different model behavior. A factual support answer should be low-temperature. A brainstorming endpoint can use higher temperature. Long-form generation needs a larger token limit than classification.

## Concept in Plain English

Chat options are knobs sent to the model provider. Common ones:

| Option | Meaning | Typical value |
|---|---|---|
| temperature | Randomness | `0.0` to `0.4` for factual answers |
| max tokens | Output length cap | Depends on endpoint |
| top-p | Alternative randomness control | Usually leave default |
| model | Which model to call | Provider-specific |

## Deeper Mechanics

Spring AI exposes common options and provider-specific options. Keep most options in configuration. Allow per-request overrides only when the endpoint genuinely needs them.

## Code Example

```java
var options = OpenAiChatOptions.builder()
        .model(settings.getModel())
        .temperature(request.temperature() != null ? request.temperature() : settings.getTemperature())
        .maxTokens(settings.getMaxTokens())
        .build();
```

For Ollama:

```java
var options = OllamaChatOptions.builder()
        .model(settings.getModel())
        .temperature(temperature)
        .build();
```

## Common Pitfalls

| Pitfall | Result | Fix |
|---|---|---|
| Exposing every provider option to clients | Hard to govern | Whitelist only safe options |
| High temperature for factual work | More hallucination | Default low |
| Unlimited output | Cost and latency spikes | Set max tokens |
| Provider-specific options everywhere | Lock-in | Hide behind app config |

## When To Use

Allow request-level options for demos, internal tools, and endpoints where the user explicitly controls style. For customer-facing production APIs, keep options server-owned.

## Production Considerations

Validate ranges server-side. Do not trust client-provided model names, token limits, or provider names unless the user is authorized to control cost.

## Interview Questions

1. What does temperature control?
2. Why should max tokens be capped?
3. How do you avoid leaking provider-specific options across your app?

## Further Reading

- Spring AI model options
- Provider-specific Spring AI chat options
