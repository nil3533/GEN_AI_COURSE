# What Is RAG and Why It Solves Hallucination

RAG means Retrieval-Augmented Generation. Before the model answers, the application retrieves relevant source chunks and puts them into the prompt.

The model still generates the final text, but it is now grounded in retrieved context.

```text
Question -> retrieve relevant chunks -> prompt model with chunks -> answer with citations
```

RAG reduces hallucination because the model has fresh, domain-specific facts. It does not eliminate hallucination because the model can still ignore context, overgeneralize, or cite weak evidence.

Use RAG when the answer depends on private, current, or large knowledge that does not fit inside the model prompt.
