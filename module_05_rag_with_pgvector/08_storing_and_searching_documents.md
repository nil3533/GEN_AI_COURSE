# Storing and Searching Documents

Ingestion stores chunks:

```text
document -> chunks -> embeddings -> vector rows
```

Question answering searches chunks:

```text
question -> embedding -> similarity search -> top-k chunks
```

The retrieved chunks become the grounded prompt. The response should include citations so the user can inspect where the answer came from.
