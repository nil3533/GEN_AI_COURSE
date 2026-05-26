# The Spring AI RAG Pipeline

A normal RAG pipeline has five steps:

1. Load documents.
2. Split documents into chunks.
3. Create embeddings for each chunk.
4. Store embeddings in a vector store.
5. Retrieve chunks and pass them to `ChatClient`.

In Spring Boot, keep these as separate services:

```text
DocumentController -> RagService -> Chunker -> EmbeddingGateway -> VectorRepository -> AnswerGateway
```

That shape keeps tests deterministic. You can test chunking, retrieval, and citation logic without a live LLM.
