# Mini-Project 5: Chat with Docs using Spring AI and pgvector

> Module 5 capstone - document ingestion, embeddings, pgvector retrieval, and grounded answers with citations

## Goal

This project builds a small RAG service. It ingests documents, splits them into chunks, embeds each chunk, stores vectors, retrieves relevant chunks for a question, and returns an answer with citations.

Default tests use deterministic in-memory components. The `pgvector` profile uses PostgreSQL + pgvector.

## Endpoints

| Endpoint | Purpose |
|---|---|
| `POST /api/documents/ingest` | Ingest one text/Markdown document |
| `GET /api/documents` | List ingested documents |
| `DELETE /api/documents` | Delete all chunks |
| `POST /api/rag/ask` | Ask a grounded RAG question |
| `POST /api/rag/eval` | Run a small retrieval eval set |
| `GET /actuator/health` | Confirm the app is running |

## Run Tests

```powershell
cd F:\GEN_AI_COURSE\module_05_rag_with_pgvector\mini_project
mvn test
```

## Run with In-Memory Store

This does not need Docker or Ollama. It is useful for quickly testing the API shape.

```powershell
cd F:\GEN_AI_COURSE\module_05_rag_with_pgvector\mini_project
mvn spring-boot:run
```

## Run with pgvector

Start PostgreSQL + pgvector:

```powershell
cd F:\GEN_AI_COURSE\module_05_rag_with_pgvector\mini_project
docker compose up -d
```

Run the app with pgvector and deterministic hash embeddings:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=pgvector"
```

## Run with pgvector and Ollama

```powershell
F:\Ollama\ollama.exe serve
F:\Ollama\ollama.exe pull nomic-embed-text
F:\Ollama\ollama.exe pull llama3.2:3b

cd F:\GEN_AI_COURSE\module_05_rag_with_pgvector\mini_project
docker compose up -d
mvn spring-boot:run "-Dspring-boot.run.profiles=pgvector,ollama"
```

## Sample Assets

Use `sample-docs/` for demo content and `api-examples.http` for IDE HTTP clients.

## API Examples

Ingest:

```powershell
curl.exe -X POST http://localhost:8083/api/documents/ingest `
  -H "Content-Type: application/json" `
  -d "{\"documentId\":\"spring-ai-notes\",\"title\":\"Spring AI Notes\",\"source\":\"manual\",\"content\":\"Spring AI ChatClient is the fluent API for calling chat models. RAG retrieves relevant context before asking the model to answer. pgvector stores embeddings in PostgreSQL.\"}"
```

Ask:

```powershell
curl.exe -X POST http://localhost:8083/api/rag/ask `
  -H "Content-Type: application/json" `
  -d "{\"question\":\"What is ChatClient used for?\",\"topK\":3}"
```

List documents:

```powershell
curl.exe http://localhost:8083/api/documents
```

Evaluate retrieval:

```powershell
curl.exe -X POST http://localhost:8083/api/rag/eval
```

## Response Shape

```json
{
  "question": "What is ChatClient used for?",
  "answer": "ChatClient is used for calling chat models.",
  "sources": [
    {
      "documentId": "spring-ai-notes",
      "title": "Spring AI Notes",
      "source": "manual",
      "chunkIndex": 0,
      "chunkText": "Spring AI ChatClient is the fluent API...",
      "relevanceScore": 0.84
    }
  ]
}
```

## Key Files

```text
src/main/java/com/sani/ragdocs/controller/RagController.java
src/main/java/com/sani/ragdocs/service/RagService.java
src/main/java/com/sani/ragdocs/service/TextChunker.java
src/main/java/com/sani/ragdocs/embedding/HashEmbeddingGateway.java
src/main/java/com/sani/ragdocs/embedding/OllamaEmbeddingGateway.java
src/main/java/com/sani/ragdocs/store/InMemoryVectorRepository.java
src/main/java/com/sani/ragdocs/store/PgVectorRepository.java
src/main/java/com/sani/ragdocs/answer/TemplateAnswerGateway.java
src/main/java/com/sani/ragdocs/answer/SpringAiAnswerGateway.java
```

## What This Teaches

- how a RAG pipeline is assembled in Spring Boot
- how chunking changes retrieval
- how pgvector stores and searches embeddings
- how to return source citations
- how to evaluate retrieval with recall@k
- how to keep tests deterministic while still supporting live providers
