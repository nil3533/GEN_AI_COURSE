# Advanced Query Rewriting and HyDE

Sometimes the user question is too short or vague for good retrieval.

Query rewriting expands it:

```text
"memory in spring ai" -> "Spring AI chat memory advisors conversation history storage"
```

HyDE asks the model to create a hypothetical answer, embeds that answer, and uses it for retrieval. It can help with vague queries, but it can also pull retrieval toward hallucinated assumptions.
