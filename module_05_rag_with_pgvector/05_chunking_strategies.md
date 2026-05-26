# Chunking Strategies

Chunking controls what the retriever can find.

Small chunks:

- precise
- cheap
- may miss context

Large chunks:

- more context
- more expensive
- can dilute relevance

Start with character or token chunks plus overlap:

```text
chunk size: 900 characters
overlap: 120 characters
```

Use document-aware chunking later for legal contracts, source code, API docs, and chat logs.
