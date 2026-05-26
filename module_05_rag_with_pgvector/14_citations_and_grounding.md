# Citations and Grounding

Citations make RAG inspectable.

Each source should include:

```text
documentId
title
source
chunkIndex
relevanceScore
chunkText
```

The model should answer only from retrieved context. If the context is weak, the right answer is:

```text
I do not have enough information in the indexed documents.
```

That is better than a confident unsupported answer.
