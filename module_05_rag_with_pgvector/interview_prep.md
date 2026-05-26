# Module 5 Interview Prep

## Short Answers

### What is RAG?

RAG retrieves relevant source chunks before generation, then asks the model to answer using that retrieved context.

### Why use pgvector?

It keeps vector search inside PostgreSQL, which many Spring teams already operate. It is a strong default before adopting a dedicated vector database.

### What causes bad RAG answers?

Bad ingestion, poor chunking, weak embeddings, low recall, missing citations, prompt leakage, or the model ignoring context.

### What is recall@k?

It measures whether the expected source appears in the top k retrieved chunks.

## Debugging Scenarios

### The answer hallucinates

Check retrieved chunks first. If retrieval is bad, fix chunking, embeddings, or search. If retrieval is good, tighten the grounded prompt.

### The correct document is never retrieved

Check chunk text, metadata filters, embedding model, vector dimensions, and exact keyword needs.

### Retrieval is slow

Add indexes, reduce candidate set with metadata filters, tune pgvector index type, and keep chunk count reasonable.
