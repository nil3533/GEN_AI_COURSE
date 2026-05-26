# Reranking for Quality

Reranking is a second retrieval stage.

Flow:

```text
retrieve top 20 chunks -> rerank -> keep best 5 -> answer
```

It improves quality because the first vector search is fast but rough. A reranker can compare the question and each chunk more carefully.

This is often the biggest quality improvement after good chunking.
