# Evaluating RAG

RAG evaluation starts with retrieval.

Useful metrics:

| Metric | Meaning |
|---|---|
| recall@k | Did the expected document appear in the top k results? |
| MRR | How high was the first correct result? |
| faithfulness | Does the final answer stay inside the retrieved context? |
| relevance | Did the answer actually answer the question? |

This module includes a small `/api/rag/eval` endpoint. It is not a full benchmark, but it teaches the habit of measuring retrieval instead of trusting demos.
