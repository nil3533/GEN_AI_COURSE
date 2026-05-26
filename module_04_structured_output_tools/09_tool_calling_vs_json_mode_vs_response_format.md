# Tool Calling vs JSON Mode vs Response Format

> Module 4 - File 9 of 10 - Choose the right control mechanism

## Why This Matters

Structured output and tool calling solve different problems. Mixing them without a reason creates complexity.

## Comparison

| Technique | Use for | Example |
|---|---|---|
| Structured output | Typed answer from the model | classify ticket intent |
| Tool calling | Backend data or action | get order status |
| Provider JSON mode | Provider-enforced JSON syntax | simple extraction on providers that support it |
| Response format/schema | Stronger provider-specific output control | production JSON with supported models |

## Rule of Thumb

Use structured output when code consumes the final answer. Use tool calling when the model needs facts or actions from your system. Use provider JSON mode when you need syntax enforcement and accept provider-specific behavior.

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Using tools for static knowledge | Wasted complexity |
| Using JSON output for current data | Stale answers |
| Assuming every provider supports the same controls | Runtime failures |
| No fallback when JSON mode is unavailable | Portability loss |

## Production Considerations

Keep your application contract provider-neutral where possible. Hide provider-specific options behind configuration.

## Interview Questions

1. When is tool calling unnecessary?
2. Why does provider JSON mode reduce portability?
3. Can you combine tool calling and structured output?

## Further Reading

- Spring AI structured output
- Provider-specific response format docs
