# GenAI Engineer's Glossary — Spring AI Edition

> Every term you'll encounter in the next 6 months, defined with Java/Spring analogies. Skim now, return as reference.

---

## Core GenAI concepts

**Agent** — An LLM that runs in a loop, choosing which tool to call next based on the previous result. *Spring analogy:* a `@Service` method that uses a Strategy pattern, where the LLM picks the strategy at runtime. In Spring AI, you build agents by combining `ChatClient` + `@Tool` beans + a loop (or a workflow framework).

**Attention (Self-Attention)** — The mechanism inside a transformer that lets each token "look at" every other token in the input to figure out its meaning in context. The word "bank" in "river bank" attends differently than in "savings bank." You won't implement this; you should be able to explain it.

**Chunking** — Splitting a long document into smaller pieces before embedding. Spring AI provides `TokenTextSplitter` and `TextSplitter` implementations.

**Context Window** — Maximum tokens an LLM can process per request (input + output). GPT-4o has 128K, Claude has 200K, Gemini 1.5 up to 2M. Larger = more capable, slower, more expensive.

**Embedding** — A list of floats (typically 384–1536 dims) representing the *meaning* of text. Similar meanings → similar vectors. *Spring analogy:* like a hash, except semantically similar inputs produce mathematically close outputs instead of identical ones. Spring AI exposes this via `EmbeddingModel`.

**Few-shot Prompting** — Giving the LLM 2–5 examples of the task in the prompt before asking it to do a new one. Big quality boost over zero-shot.

**Fine-tuning** — Continuing to train a pre-trained model on your data. Expensive, risky, usually overkill. Try prompt engineering + RAG first. Spring AI doesn't do fine-tuning; you'd use Hugging Face/OpenAI APIs for that.

**Foundation Model** — A large pre-trained model designed as the base for many downstream tasks. GPT-4, Claude, Llama 3, Gemini are all foundation models.

**Hallucination** — When an LLM confidently states something false. Mitigations: RAG (give it source docs), prompting ("if unsure, say so"), validation, post-checks.

**Inference** — Running a trained model to get an output. The opposite of training. 95% of what you do as an AI Engineer is inference, not training.

**LLM (Large Language Model)** — A foundation model trained on text. GPT, Claude, Llama, Mistral, Gemini.

**MCP (Model Context Protocol)** — Anthropic-originated open protocol, now industry-standard, for connecting LLMs to external tools and data sources. *Spring analogy:* like JDBC for databases, but for LLM tools. Spring AI 1.0+ ships first-class MCP client and server support.

**Multimodal** — A model that handles more than text (images, audio, video). GPT-4o, Claude 3.5/4, Gemini are multimodal.

**Prompt** — The input text you send to an LLM.

**Prompt Engineering** — The craft of writing prompts that reliably get good outputs. More art than science.

**RAG (Retrieval-Augmented Generation)** — Architecture: when the user asks something, *retrieve* relevant docs from a knowledge base, then *generate* the answer using those docs as context. Solves hallucination and lets LLMs answer about data they never saw in training.

**Reranking** — A second-stage filter after initial retrieval. Initial vector search returns 50 candidates fast; a reranker (cross-encoder model) carefully scores them and picks the best 5. Major RAG quality boost. Spring AI exposes rerankers via Cohere or custom implementations.

**System Prompt** — A special prompt that sets the LLM's persona and constraints, separate from user messages. *Spring analogy:* config file vs. request body — system prompt = config, user message = request.

**Temperature** — Parameter (0.0–2.0 typically) controlling randomness. 0 = deterministic. 1 = balanced. 2 = chaotic. Use low for code/extraction, higher for creative writing. Set via `ChatOptions` in Spring AI.

**Token** — The unit an LLM sees. ~1 token = 4 chars of English ≈ ¾ of a word. "Hello world" is 2 tokens. Pricing and context limits are measured in tokens.

**Tool / Function Calling** — Letting an LLM invoke functions in your code. In Spring AI: annotate a bean method with `@Tool`, register it with the `ChatClient`, and the LLM calls it when needed. Spring AI handles the JSON schema generation and dispatch.

**Transformer** — The neural network architecture from the 2017 paper "Attention Is All You Need" that powers every modern LLM.

**Vector Database** — A DB optimized for storing embeddings and fast nearest-neighbor search. Spring AI's `VectorStore` interface has implementations for PGVector, Chroma, Qdrant, Pinecone, Weaviate, Milvus, Redis, Azure AI Search, and more.

**Zero-shot Prompting** — Asking the LLM to do a task with no examples. "Translate this to French: ___"

---

## Spring AI–specific concepts

**Advisor** — Spring AI's interception layer for `ChatClient` calls. Wraps every prompt + response with cross-cutting logic. *Spring analogy:* exactly AOP for LLM calls. Built-in Advisors include `MessageChatMemoryAdvisor`, `QuestionAnswerAdvisor` (RAG), `SafeGuardAdvisor` (content filter). You can write your own for logging, cost tracking, retries.

**BeanOutputConverter** — Spring AI utility that turns an LLM's response into a strongly-typed Java object (record or POJO). It generates a JSON schema from your class, injects it into the prompt, and parses the LLM's reply. *Java analogy:* like Jackson's `readValue` but with an LLM in the middle.

**ChatClient** — The fluent API for calling an LLM in Spring AI. The main entry point. Example: `chatClient.prompt().user("Hello").call().content()`. Replaces the lower-level `ChatModel` for most use cases.

**ChatMemory** — Spring AI's abstraction for storing conversation history per session. Implementations include `InMemoryChatMemory`, `JdbcChatMemory`, `CassandraChatMemory`. Used via `MessageChatMemoryAdvisor`.

**ChatModel** — The low-level model abstraction. One per provider (`OpenAiChatModel`, `OllamaChatModel`, etc.). You usually go through `ChatClient` instead.

**ChatOptions** — Per-request options: model, temperature, max tokens, etc. Type-safe via builder. Each provider has its own options class (`OpenAiChatOptions.builder()...`) but the base `ChatOptions` interface is portable.

**Document** — Spring AI's representation of a chunk of text plus metadata. Used everywhere in RAG. `new Document(content, Map.of("source", "manual.pdf"))`.

**DocumentReader** — Loads documents from a source. Implementations: `TikaDocumentReader` (PDFs, Word, HTML), `JsonReader`, `TextReader`, `MarkdownDocumentReader`. *Spring analogy:* like a `Resource` reader, specialized for unstructured content.

**EmbeddingModel** — Spring AI interface for generating embeddings from text. Implementations per provider — `OpenAiEmbeddingModel`, `OllamaEmbeddingModel`, etc.

**MCP Client / Server (Spring AI)** — Spring AI's modules for the Model Context Protocol. Client: connect to external MCP servers. Server: expose your Spring beans as MCP-compatible tools. Spring Boot starters: `spring-ai-starter-mcp-client`, `spring-ai-starter-mcp-server`.

**Message** — A turn in a conversation: `SystemMessage`, `UserMessage`, `AssistantMessage`, `ToolResponseMessage`. Spring AI uses these to model multi-turn chats.

**Prompt** — Spring AI's wrapper class around a list of `Message`s plus options. You usually don't construct this directly; the `ChatClient` builder handles it.

**PromptTemplate** — A string template with placeholders, rendered using a `TemplateRenderer` (StringTemplate by default). `new PromptTemplate("Translate {text} to {language}").render(Map.of(...))`.

**TokenTextSplitter** — Default chunker in Spring AI. Splits text by token count with configurable chunk size and overlap.

**Tool / @Tool** — A method (or function) the LLM can call. Annotate a Spring bean method with `@Tool` and describe it; Spring AI generates the JSON schema, exposes it to the LLM, and dispatches calls back to your method.

**ToolCallback / ToolCallbackProvider** — Lower-level mechanism behind `@Tool`. Used when you want to register tools dynamically (e.g., MCP-provided tools).

**VectorStore** — Spring AI's interface for vector databases. Core methods: `add(List<Document>)`, `similaritySearch(SearchRequest)`, `delete(...)`. Implementations per backend (`PgVectorStore`, `ChromaVectorStore`, etc.).

---

## Spring AI architecture cheat sheet

```
┌──────────────────────────────────────────────────────┐
│  Your Controller / Service                            │
│                  uses                                 │
│                    ▼                                  │
│            ┌──────────────┐                          │
│            │  ChatClient  │  ← fluent API            │
│            └──────┬───────┘                          │
│                   │                                   │
│         ┌─────────┴─────────┐                        │
│         │                   │                        │
│      Advisors          ChatModel                     │
│   (memory, RAG,      (provider-specific:             │
│    logging, etc.)    OpenAI, Ollama, ...)            │
│                                                       │
│  RAG path also uses:                                  │
│    VectorStore ← DocumentReader → TokenTextSplitter  │
│        ↑                                              │
│    EmbeddingModel                                     │
│                                                       │
│  Tools:                                               │
│    @Tool methods → ToolCallbackProvider → LLM        │
└──────────────────────────────────────────────────────┘
```

You'll come back to this diagram many times. Bookmark it.

---

## Java AI ecosystem comparison

| Framework | What it is | When to pick it |
|---|---|---|
| **Spring AI** | Spring-team's official, opinionated, integrates deeply with Spring Boot | You're on Spring Boot (this is you) |
| **LangChain4j** | Framework-agnostic Java port of LangChain; "Lego approach" | You're on Quarkus/Micronaut, or want max provider coverage, or want fine-grained building blocks |
| **Semantic Kernel (Java SDK)** | Microsoft's framework, official Java port | You're heavily on Azure or have C# colleagues using the same patterns |
| **DJL (Deep Java Library)** | For running models inside the JVM (no API calls) | Edge inference, embedded ML — not LLM-orchestration |

For your scope (Spring Boot + LLM APIs + RAG + agents), **Spring AI is the right choice**. Mentioning LangChain4j in interviews shows you considered alternatives.

---

## Acronyms

| Acronym | Expansion | One-liner |
|---|---|---|
| LLM | Large Language Model | The text-generating AI |
| GenAI | Generative AI | AI that creates content |
| RAG | Retrieval-Augmented Generation | LLM + your documents |
| MCP | Model Context Protocol | Standard for LLM ↔ tools |
| LoRA | Low-Rank Adaptation | Cheap fine-tuning |
| QLoRA | Quantized LoRA | Even cheaper, laptop-scale |
| SFT | Supervised Fine-Tuning | Standard fine-tuning |
| RLHF | Reinforcement Learning from Human Feedback | How ChatGPT was aligned |
| HNSW | Hierarchical Navigable Small World | The index algorithm pgvector uses |
| ANN | Approximate Nearest Neighbor | What vector DBs do |
| TPM / RPM | Tokens-per-minute / Requests-per-minute | API rate limit units |
| SSE | Server-Sent Events | HTTP protocol for streaming (used for LLM streaming) |
| AOP | Aspect-Oriented Programming | Spring's pattern; Advisors are AOP for LLM calls |
| HyDE | Hypothetical Document Embeddings | Advanced RAG query technique |

---

When in doubt, search this file first. If a term is missing, add it — this is your living document.
