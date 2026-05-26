# The Tool Annotation in Spring AI

> Module 4 - File 6 of 10 - Expose Spring methods as model-callable tools

## Why This Matters

`@Tool` lets you publish existing Java methods to the model without turning your business service into prompt code.

## Concept in Plain English

Annotate a method, describe it clearly, and register the object with `ChatClient`.

## Code Example

```java
@Component
public class OrderTools {

    private final OrderService orderService;

    public OrderTools(OrderService orderService) {
        this.orderService = orderService;
    }

    @Tool(name = "getRecentOrders", description = "List recent orders for one customer id.")
    public List<OrderSummary> getRecentOrders(
            @ToolParam(description = "Customer id such as cust-100") String customerId) {
        return orderService.getRecentOrders(customerId);
    }
}
```

Register:

```java
var toolProvider = MethodToolCallbackProvider.builder()
        .toolObjects(orderTools)
        .build();

chatClient.prompt()
        .toolCallbacks(toolProvider)
        .user(userMessage)
        .call()
        .content();
```

## Common Pitfalls

| Pitfall | Result |
|---|---|
| Missing parameter descriptions | Poor argument selection |
| Returning huge objects | Token waste |
| Throwing raw business exceptions | Confusing final answers |
| Reusing public admin methods as tools | Overpowered assistant |

## When To Use

Use `@Tool` for stable, narrow methods that you would be comfortable exposing through a normal API after authorization.

## Production Considerations

Keep tool methods thin. Delegate real logic to services where you already have tests, validation, and transactions.

## Interview Questions

1. What belongs in a tool description?
2. Should tools contain business logic?
3. How do you register multiple tools?

## Further Reading

- Spring AI `@Tool`
- Spring AI `MethodToolCallbackProvider`
