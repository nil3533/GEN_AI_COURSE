# Module 4 Mini-Project Walkthrough

This project is an order assistant. It shows how Spring Boot can use an AI model to understand a customer request, call real Java backend tools, and return a structured JSON response.

The important idea:

```text
AI understands the message.
Java owns the data and business rules.
Tools are the safe bridge between AI and Java.
```

## What the Application Does

The app exposes one main assistant API:

```text
POST /api/assistant
```

Example request:

```json
{
  "customerId": "cust-100",
  "message": "Show my recent orders",
  "confirmed": false
}
```

Expected response shape:

```json
{
  "intent": "RECENT_ORDERS",
  "answer": "You have 2 recent orders: ORD-1002 is PROCESSING and ORD-1001 is SHIPPED.",
  "actionRequired": false,
  "toolsCalled": [],
  "safetyNotes": [],
  "confidence": 0.9
}
```

## High-Level Architecture

```text
Postman / curl
    |
    v
AssistantController
    |
    v
AssistantService
    |
    v
SpringAiAssistantGateway
    |
    v
Spring AI ChatClient
    |
    v
Groq / Ollama model
    |
    | model can call tools
    v
OrderTools
    |
    v
OrderService
    |
    v
In-memory seeded orders
```

## Main Packages

```text
com.sani.orderassistant.controller
    REST APIs live here.

com.sani.orderassistant.service
    Assistant orchestration and Spring AI gateway live here.

com.sani.orderassistant.tools
    AI-callable backend tools live here.

com.sani.orderassistant.order
    Real order business data and rules live here.

com.sani.orderassistant.dto
    API request and response records live here.

com.sani.orderassistant.config
    Provider configuration properties live here.
```

## Code Call Flow: POST /api/assistant

### 1. Request enters the controller

File:

```text
src/main/java/com/sani/orderassistant/controller/AssistantController.java
```

Method:

```java
@PostMapping("/assistant")
public AssistantResponse assistant(@Valid @RequestBody AssistantRequest request) {
    return assistantService.respond(request);
}
```

Responsibility:

- accepts the HTTP request
- validates the JSON body
- passes the request to `AssistantService`

It does not contain AI logic.

## 2. Request is represented as AssistantRequest

File:

```text
src/main/java/com/sani/orderassistant/dto/AssistantRequest.java
```

Fields:

```java
String customerId
String message
boolean confirmed
```

Meaning:

- `customerId`: which customer is asking
- `message`: natural language user request
- `confirmed`: safety flag for write actions like cancellation

Example:

```json
{
  "customerId": "cust-100",
  "message": "Cancel order ORD-1002",
  "confirmed": false
}
```

## 3. AssistantService prepares the AI run

File:

```text
src/main/java/com/sani/orderassistant/service/AssistantService.java
```

Main method:

```java
public AssistantResponse respond(AssistantRequest request)
```

What it does:

1. Reads active provider settings from `application.yml`.
2. Opens a tool recording scope through `ToolCallRecorder`.
3. Calls `AssistantGateway.generate(...)`.
4. Adds actual tool traces to the final response.
5. Adds a safety note if a mutating tool was used without confirmation.

Important line:

```java
toolCallRecorder.open(request.customerId(), request.confirmed(), properties.getMaxToolCalls())
```

This stores request-level context for tools:

- current customer id
- whether cancellation was confirmed
- max tool calls allowed

## 4. SpringAiAssistantGateway calls the model

File:

```text
src/main/java/com/sani/orderassistant/service/SpringAiAssistantGateway.java
```

Main method:

```java
public AssistantResponse generate(AssistantRequest request, ProviderSettings settings)
```

What it does:

1. Builds a `ChatClient`.
2. Adds a system prompt.
3. Registers the Java tools.
4. Sends the user message to the model.
5. Asks Spring AI to parse the final model answer into `AssistantResponse`.

Important code:

```java
return createChatClient(settings).prompt()
        .system(...)
        .user(userPrompt(request))
        .toolCallbacks(orderToolProvider)
        .call()
        .entity(converter);
```

This is where the AI work happens.

## 5. The System Prompt Tells the Model How to Behave

File:

```text
src/main/java/com/sani/orderassistant/service/SpringAiAssistantGateway.java
```

The system prompt says:

```text
You are a customer order assistant.
Use tools when the user asks about order status, recent orders, or cancellation.
Do not invent order data.
Never claim an order was cancelled unless the cancelOrder tool returned status CANCELLED.
Return valid JSON matching AssistantResponse.
```

Purpose:

- tells the model when to call tools
- tells it not to invent order data
- forces the final answer to be structured JSON

## 6. BeanOutputConverter Creates Structured Output

File:

```text
src/main/java/com/sani/orderassistant/service/SpringAiAssistantGateway.java
```

Code:

```java
var converter = new BeanOutputConverter<>(AssistantResponse.class);
```

This tells Spring AI:

```text
The final model response must fit the AssistantResponse Java record.
```

Final response type:

```text
src/main/java/com/sani/orderassistant/dto/AssistantResponse.java
```

Fields:

```java
AssistantIntent intent
String answer
boolean actionRequired
List<ToolCallTrace> toolsCalled
List<String> safetyNotes
double confidence
```

This is why the API returns typed JSON instead of plain text.

## 7. OrderTools Are the AI-Callable Java Methods

File:

```text
src/main/java/com/sani/orderassistant/tools/OrderTools.java
```

Tools:

```java
getOrderStatus(orderId)
getRecentOrders(customerId)
cancelOrder(orderId, reason)
```

Each method has `@Tool`, so Spring AI can expose it to the model.

Example:

```java
@Tool(
    name = "getRecentOrders",
    description = "List recent orders for the current customer..."
)
public List<OrderSummary> getRecentOrders(String customerId)
```

When the user says:

```text
Show my recent orders
```

The model should choose:

```text
getRecentOrders
```

Then Java executes:

```java
orderService.recentOrders(customerId)
```

## 8. OrderService Owns the Real Data

File:

```text
src/main/java/com/sani/orderassistant/order/OrderService.java
```

This is normal Java business logic. It does not depend on AI.

Seeded data:

```text
cust-100 -> ORD-1001 -> Spring AI Handbook -> SHIPPED
cust-100 -> ORD-1002 -> Java 21 Workshop Pass -> PROCESSING
cust-200 -> ORD-2001 -> pgvector Starter Kit -> DELIVERED
```

Main methods:

```java
recentOrders(customerId)
getOrderStatus(customerId, orderId)
cancelOrder(customerId, orderId, reason)
```

Important:

The AI cannot directly read or write orders. It can only access data through `OrderTools`, which call `OrderService`.

## 9. ToolCallRecorder Tracks Tool Usage

File:

```text
src/main/java/com/sani/orderassistant/tools/ToolCallRecorder.java
```

Purpose:

- stores request-level customer id
- stores confirmation flag
- limits tool calls
- records every tool call

Example trace:

```json
{
  "name": "getRecentOrders",
  "arguments": {
    "customerId": "cust-100"
  },
  "result": "[{\"orderId\":\"ORD-1002\",\"status\":\"PROCESSING\"}]",
  "mutating": false
}
```

This helps debug what the AI actually did.

## Full Example: Show My Orders

Request:

```json
{
  "customerId": "cust-100",
  "message": "Show my recent orders",
  "confirmed": false
}
```

Expected path:

```text
AssistantController.assistant()
    -> AssistantService.respond()
        -> ToolCallRecorder.open(customerId=cust-100)
        -> SpringAiAssistantGateway.generate()
            -> ChatClient sends prompt to model
            -> model chooses getRecentOrders
                -> OrderTools.getRecentOrders("cust-100")
                    -> OrderService.recentOrders("cust-100")
            -> model creates AssistantResponse JSON
        -> AssistantService adds tool traces
    -> API returns AssistantResponse
```

## Full Example: Cancel Order

Request without confirmation:

```json
{
  "customerId": "cust-100",
  "message": "Cancel order ORD-1002",
  "confirmed": false
}
```

Expected behavior:

```text
cancelOrder tool refuses cancellation
```

Why:

```java
if (!recorder.cancellationConfirmed()) {
    return new CancelOrderResult(orderId, "REJECTED", "Cancellation requires explicit confirmation.");
}
```

Request with confirmation:

```json
{
  "customerId": "cust-100",
  "message": "Cancel order ORD-1002",
  "confirmed": true
}
```

Expected behavior:

```text
cancelOrder tool calls OrderService.cancelOrder(...)
ORD-1002 changes from PROCESSING to CANCELLED
```

## API List

### Health

```text
GET /actuator/health
```

### Provider Summary

```text
GET /api/assistant/providers
```

Use this to check whether Groq or Ollama is active.

### Raw Seeded Orders

```text
GET /api/orders/cust-100
```

This bypasses AI and directly checks `OrderService`.

### Main Assistant API

```text
POST /api/assistant
```

Body:

```json
{
  "customerId": "cust-100",
  "message": "Show my recent orders",
  "confirmed": false
}
```

## How to Debug Blank Answers

If `/api/assistant` returns a blank `answer`, check these fields first:

```json
{
  "answer": "",
  "toolsCalled": [],
  "safetyNotes": []
}
```

Interpretation:

| What you see | Meaning |
|---|---|
| `toolsCalled` is empty | The model probably did not call a tool |
| `toolsCalled` has `getRecentOrders` | Tool ran; final answer generation was weak |
| `safetyNotes` has confirmation text | A write action was blocked correctly |

To confirm the data exists, call:

```text
GET /api/orders/cust-100
```

If that returns orders, the issue is not `OrderService`; it is the AI/tool-selection path.

For clearer tool selection, use direct wording:

```text
Show my recent orders
```

instead of:

```text
show my order
```

The second wording is ambiguous: it could mean one order, all orders, or a missing order id.

## What This Project Teaches

This project teaches four production ideas:

1. AI should not own business data.
2. AI can choose tools, but Java must enforce rules.
3. Final AI output should be typed and validated.
4. Tool traces are required for debugging and audit.

## Mental Model

Think of the model as a smart router:

```text
User says something messy
    -> model understands intent
    -> model chooses a safe Java tool
    -> Java returns facts
    -> model writes a clean final answer
```

The model is not the database, not the security layer, and not the business rule engine. Those stay in Spring Boot.
