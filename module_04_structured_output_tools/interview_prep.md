# Module 4 Interview Prep

## Short Answers

### Why use structured output?

Because backend code needs stable fields. Structured output lets the model return data that can be parsed into Java records, validated, tested, and routed.

### What does `BeanOutputConverter` do?

It generates format instructions from a Java type and converts model text back into that type. It helps avoid fragile prose parsing.

### What is tool calling?

Tool calling lets the model choose from application-provided functions. The application executes the selected Java method and returns the result to the model so it can answer with current or private data.

### Why are tool descriptions important?

The model uses names, descriptions, and argument descriptions to decide which tool to call and what arguments to provide. Descriptions are part of runtime behavior.

### How do you make `cancelOrder` safe?

Require explicit confirmation, validate order ownership, enforce normal business rules, trace the call, and limit tool calls per request.

## Debugging Scenarios

### The model returns prose instead of JSON

Check that the prompt includes converter format instructions, lower temperature, simplify the schema, and consider one retry with a stronger repair prompt.

### The wrong tool is called

Review tool names and descriptions. Remove overlap, add negative guidance, and log tool-call traces for replay.

### The model calls tools too many times

Set a hard budget, reduce the number of tools registered, and make tool results concise enough for the model to answer after one call.

### Cancel order happened without confirmation

This is an application bug, not a model bug. Write tools must enforce confirmation and authorization inside the tool or service layer.

## Practice Prompts

1. Explain `BeanOutputConverter` to a Spring Boot engineer.
2. Design an assistant that can check order status but cannot cancel orders.
3. Compare structured output, tool calling, and RAG.
4. Explain how you would audit tool calls in production.
