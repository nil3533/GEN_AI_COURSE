# Introduction to Tool Calling

> Module 4 - File 5 of 10 - Let the model call backend capabilities

## Why This Matters

Models know language. Your services know facts and can take action. Tool calling connects the two.

## Concept in Plain English

Tool calling flow:

1. User asks a question.
2. Model decides a tool is needed.
3. Model emits a tool call with arguments.
4. Your application executes the Java method.
5. Tool result is sent back to the model.
6. Model writes the final answer.

## Code Example

```java
@Tool(name = "getOrderStatus", description = "Get current status for one order id.")
public OrderStatusResult getOrderStatus(
        @ToolParam(description = "Order id such as ORD-1001") String orderId) {
    return orderService.getOrderStatus(orderId);
}
```

```java
String answer = chatClient.prompt()
        .system("Use tools when order data is needed.")
        .tools(orderTools)
        .user("Where is ORD-1001?")
        .call()
        .content();
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Tools with vague names | Wrong tool selection |
| Exposing unsafe write methods | Accidental mutations |
| No argument validation | Hallucinated ids hit services |
| No call budget | Loops and cost spikes |

## When To Use

Use tool calling when the model needs current data, private data, calculations, or controlled actions.

## Production Considerations

Tool authorization is your job. The model choosing a tool does not mean the user is allowed to execute it.

## Interview Questions

1. What does the model send when it calls a tool?
2. Why should tools be narrow?
3. How do you protect write tools?

## Further Reading

- Spring AI Tools
- Module 7 agents and MCP
