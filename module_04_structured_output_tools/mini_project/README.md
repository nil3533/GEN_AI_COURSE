# Mini-Project 4: Order Assistant with Structured Output and Tools

> Module 4 capstone - Spring AI typed responses plus backend tool calls

## Goal

This project builds an order assistant that can answer order questions using real Java backend methods. The final answer is a typed `AssistantResponse`, and every tool call is traced with arguments and a result summary.

## Endpoints

| Endpoint | Purpose |
|---|---|
| `POST /api/assistant` | Ask the order assistant |
| `GET /api/assistant/providers` | Show active provider and safe provider configuration |
| `GET /api/orders/{customerId}` | Inspect seeded demo orders for one customer |
| `GET /actuator/health` | Confirm the app is running |

Request body:

```json
{
  "customerId": "cust-100",
  "message": "What is the status of order ORD-1001?",
  "confirmed": false
}
```

Response shape:

```json
{
  "intent": "ORDER_STATUS",
  "answer": "Order ORD-1001 has shipped and is expected on 2026-05-30.",
  "actionRequired": false,
  "toolsCalled": [
    {
      "name": "getOrderStatus",
      "arguments": {
        "orderId": "ORD-1001"
      },
      "result": "{\"orderId\":\"ORD-1001\",\"status\":\"SHIPPED\"}",
      "mutating": false
    }
  ],
  "safetyNotes": [],
  "confidence": 0.9
}
```

## Run Tests

```powershell
cd F:\GEN_AI_COURSE\module_04_structured_output_tools\mini_project
mvn test
```

Latest validation:

- 2026-05-26: `mvn test` passed locally, 10 tests run, 0 failures.

## Run with Groq

Groq is the recommended provider for this module because tool calling support is model/provider dependent.

```powershell
$env:GROQ_API_KEY="your_key_here"

cd F:\GEN_AI_COURSE\module_04_structured_output_tools\mini_project
mvn spring-boot:run -Dspring-boot.run.profiles=groq
```

The app runs on:

```text
http://localhost:8082
```

## API Tests

Check providers:

```powershell
curl.exe http://localhost:8082/api/assistant/providers
```

Inspect demo orders:

```powershell
curl.exe http://localhost:8082/api/orders/cust-100
```

Order status:

```powershell
curl.exe -X POST http://localhost:8082/api/assistant `
  -H "Content-Type: application/json" `
  -d "{\"customerId\":\"cust-100\",\"message\":\"What is the status of order ORD-1001?\"}"
```

Recent orders:

```powershell
curl.exe -X POST http://localhost:8082/api/assistant `
  -H "Content-Type: application/json" `
  -d "{\"customerId\":\"cust-100\",\"message\":\"Show my recent orders\"}"
```

Cancellation without confirmation:

```powershell
curl.exe -X POST http://localhost:8082/api/assistant `
  -H "Content-Type: application/json" `
  -d "{\"customerId\":\"cust-100\",\"message\":\"Cancel order ORD-1002 because I changed my mind\",\"confirmed\":false}"
```

Cancellation with confirmation:

```powershell
curl.exe -X POST http://localhost:8082/api/assistant `
  -H "Content-Type: application/json" `
  -d "{\"customerId\":\"cust-100\",\"message\":\"Cancel order ORD-1002 because I changed my mind\",\"confirmed\":true}"
```

## Seeded Demo Data

| Customer | Order | Item | Status |
|---|---|---|---|
| `cust-100` | `ORD-1001` | Spring AI Handbook | `SHIPPED` |
| `cust-100` | `ORD-1002` | Java 21 Workshop Pass | `PROCESSING` |
| `cust-200` | `ORD-2001` | pgvector Starter Kit | `DELIVERED` |

## Key Files

```text
src/main/java/com/sani/orderassistant/service/AssistantService.java
src/main/java/com/sani/orderassistant/service/SpringAiAssistantGateway.java
src/main/java/com/sani/orderassistant/tools/OrderTools.java
src/main/java/com/sani/orderassistant/tools/ToolCallRecorder.java
src/main/java/com/sani/orderassistant/order/OrderService.java
src/main/java/com/sani/orderassistant/dto/AssistantResponse.java
```

## What This Teaches

- how to combine tool calling and structured final output
- how to expose narrow backend methods with `@Tool`
- how to trace tool calls for debugging
- how to enforce write-action confirmation outside the model
- how to keep default tests free of live LLM calls
