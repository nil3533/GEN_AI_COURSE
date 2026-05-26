# QuestionAnswerAdvisor Native RAG

Spring AI includes advisor patterns for RAG. Advisors are useful when you want retrieval to be attached directly to a `ChatClient` call.

For learning, this module keeps retrieval explicit:

```text
RagService retrieves chunks
AnswerGateway builds the grounded answer
Controller returns citations
```

Explicit retrieval is easier to debug before you move to advisors.
