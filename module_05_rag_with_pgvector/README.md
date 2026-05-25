# Module 5 — RAG with pgvector

> **Weeks 8–9 · ~18 hours**

> **The single most important module in this course.** RAG is *the* dominant production architecture for enterprise LLM apps. If you nail nothing else, nail this. And because you already know PostgreSQL deeply, pgvector lets you reuse that knowledge directly.

---

## What you'll walk away with

The ability to design, build, and tune a production-grade RAG pipeline end-to-end in Spring Boot: document ingestion → chunking → embeddings → pgvector storage → retrieval → reranking → answer generation with citations. You'll also know advanced techniques (hybrid search, multi-query retrieval, contextual compression) and how to evaluate whether your RAG is actually working.

By the end you'll be able to convincingly answer: *"Design a RAG system over 10 million enterprise documents using Spring Boot."*

---

## Files (outline)

1. `01_what_is_rag_and_why_it_solves_hallucination.md` — The problem RAG solves, with architecture diagrams
2. `02_the_spring_ai_rag_pipeline.md` — `DocumentReader → TextSplitter → EmbeddingModel → VectorStore → ChatClient` — the full pipeline
3. `03_pgvector_setup_and_internals.md` — Postgres + pgvector extension; HNSW vs IVFFlat indexes; tuning `m`, `ef_construction`, `ef_search`; cosine vs L2 distance
4. `04_document_loading_pdfs_html_markdown.md` — `TikaDocumentReader`, `MarkdownDocumentReader`, custom readers; metadata handling
5. `05_chunking_strategies.md` — `TokenTextSplitter`, semantic chunking, document-aware chunking for code/contracts/chat logs
6. `06_embedding_model_selection.md` — OpenAI ada-3, BGE, Voyage, Ollama's nomic-embed-text; dimensions, cost, multilingual support; **how to pick**
7. `07_pgvector_in_spring_ai_application_yml.md` — All the properties; auto-schema-init; manual schema for production
8. `08_storing_and_searching_documents.md` — `VectorStore.add(...)`, `similaritySearch(SearchRequest)`, filters via metadata
9. `09_questionansweradvisor_native_rag.md` — Spring AI's built-in RAG Advisor; how it composes retrieval with the prompt
10. `10_advanced_query_rewriting_hyde.md` — Rewriting user queries before embedding; Hypothetical Document Embeddings
11. `11_hybrid_search_vector_plus_keyword.md` — Combining pgvector cosine search with Postgres full-text search; reciprocal rank fusion
12. `12_reranking_for_quality.md` — Two-stage retrieval with Cohere reranker or BGE-reranker; the single biggest quality win
13. `13_multi_source_retrieval.md` — RAG across multiple `VectorStore`s; result fusion
14. `14_citations_and_grounding.md` — Returning source-aware answers with `[doc_id]` markers; preventing ungrounded claims
15. `15_evaluating_rag.md` — Retrieval metrics (recall@k, MRR); generation metrics (faithfulness, relevance); building an eval harness in Java

## Mini-project

**"Chat with Spring Boot docs."** Build a Spring Boot service that:
- Ingests a directory of PDFs/Markdown (suggestion: download Spring Boot reference docs as Markdown — content you'll judge quality on)
- Uses `MarkdownDocumentReader` + `TokenTextSplitter` for chunking
- Embeds via Ollama's `nomic-embed-text` (free, local)
- Stores in pgvector via Spring AI auto-config
- Exposes `POST /api/rag/ask` returning an `AnswerWithCitations` record: `{answer, sources[]}` where sources include `documentId`, `chunkText`, `relevanceScore`
- Uses `QuestionAnswerAdvisor` for the basic version, then upgrades to manual two-stage retrieval with a reranker
- Includes a `/api/rag/eval` endpoint that runs 10 test questions and reports retrieval recall

This is a portfolio-grade project. Polish the README with screenshots, architecture diagram, and the eval report.

**Push to:** `sani-genai-journey/m05-pgvector-rag/`

## Docker Compose

This module's project needs Postgres+pgvector. Use the reusable `docker-compose.yml` from `02_ENVIRONMENT_SETUP.md`.

## Curated free resources

- Spring AI Reference — "Retrieval Augmented Generation" section (the whole thing)
- Spring AI Reference — "PGVector Vector Store" page
- Spring AI examples — `rag/` folder on GitHub
- pgvector GitHub README — index tuning advice (`github.com/pgvector/pgvector`)
- Anthropic — "Contextual Retrieval" blog post (groundbreaking 2024 technique, applicable in Spring AI)

## Interview prep highlights

- "Design RAG over 10M documents in Spring Boot. Cover chunking, embeddings, storage, retrieval, reranking, evaluation."
- "What chunking strategy for legal contracts vs source code vs chat logs?"
- "When does keyword search beat vector search?"
- "What is HyDE? When does it help, when does it hurt?"
- "Your RAG hallucinates despite retrieved context — debug it."
- "How would you migrate from pgvector to Pinecone if you outgrew Postgres?"
- "Why pgvector when there are dedicated vector DBs? When would you switch?"
- "Walk me through `QuestionAnswerAdvisor` internals."
