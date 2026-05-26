# Embedding Model Selection

An embedding model turns text into vectors. Similar text should produce similar vectors.

Common choices:

| Model | Use |
|---|---|
| Ollama `nomic-embed-text` | Free local learning |
| OpenAI embeddings | Hosted production option |
| BGE | Strong open-source retrieval |
| Voyage | High-quality hosted retrieval |

Keep one rule in mind: vector dimensions must match your table. If the model returns 768-dimensional embeddings, the pgvector column must be `vector(768)`.
