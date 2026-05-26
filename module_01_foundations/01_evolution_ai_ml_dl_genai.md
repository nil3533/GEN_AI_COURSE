# 1.1 — The Evolution: AI → ML → Deep Learning → Generative AI

> **Module 1 · File 1 of 6 · ~45 min read**

---

## 1. Why this matters

Walk into any Senior/Staff interview in 2026 and within ten minutes someone will ask: *"How is what we're doing now different from traditional ML?"* The wrong answer — "LLMs are just bigger neural networks" — gets you placed in the "hype follower" bucket. The right answer — explaining why **scale + transformer + self-supervised pre-training** created a genuine qualitative leap — places you in the "engineer who understands the fundamentals" bucket.

You also need this map for practical reasons: in many problems, classical ML is *still* the right tool. Knowing where GenAI fits prevents you from reaching for a sledgehammer when you need a screwdriver — or worse, paying ₹6L/month in OpenAI bills for a problem a logistic regression would solve for free.

---

## 2. Concept in plain English

AI as a family tree:

![AI family tree](assets/ai-family-tree.svg)

Each layer is a **subset** of the one above it. All deep learning is ML; all GenAI is deep learning; not all deep learning is GenAI. (Image classification with a CNN is deep learning but not GenAI — it predicts a label, doesn't generate content.)

The dates matter: **AI** as a field is 70 years old. **ML** is 40. **Deep learning** became practical around 2012 with AlexNet. **Generative AI** as the world knows it kicked off with GPT-3 in 2020, exploded with ChatGPT in late 2022, matured through 2024–2026. We are early.

---

## 3. Deeper mechanics — what actually changed at each layer

### Rule-Based AI (1950s–1980s)
A human expert writes thousands of if-then rules. The system applies them. *Example: MYCIN, a 1970s medical diagnosis system.* Limitation: scales linearly with rules. Falls apart when reality is messier than the rules. *Java analogy:* a Drools rule engine for everything — works until rules contradict each other.

### Classical Machine Learning (1980s–2010s)
Instead of writing rules, you give the machine **labeled examples** and an algorithm that finds the rules. Decision trees, logistic regression, SVMs, random forests. Limitation: you (the human) still had to design the **features** — "for spam detection, count exclamation marks, count uppercase words..." Feature engineering took most of a data scientist's time.

### Deep Learning (2012–2020)
A neural network is a stack of matrix multiplications with non-linear activation functions in between. "Deep" just means many layers. The key innovation: **the network learns the features itself**. You feed in raw pixels, the network's early layers learn to detect edges, middle layers detect shapes, later layers detect objects. No feature engineering. This worked because three things arrived together: more data (the internet), more compute (GPUs), better algorithms (backpropagation tricks, ReLU activations, dropout).

But: deep learning models were still **task-specific**. You trained one model for image classification, another for translation, another for sentiment analysis. Each from scratch, on labeled data. Labels are expensive.

### Generative AI (2020+) — the qualitative leap

Three things changed simultaneously:

1. **Architecture: the Transformer** (2017 paper "Attention Is All You Need"). Replaced sequential RNNs with parallel self-attention. Much faster to train, scales much better. *Covered in detail in file 02.*

2. **Self-supervised pre-training.** Instead of needing humans to label data, the model learns by predicting the next word in trillions of words of internet text. Labels come for free — the next word *is* the label. Suddenly you can train on essentially all human-written text.

3. **Scale.** Researchers (especially at OpenAI) observed an empirical pattern: **bigger models trained on more data with more compute predictably get better — and start showing capabilities the smaller versions didn't have at all.** These are "emergent capabilities." A 1B-parameter model can't do multi-step reasoning; a 100B-parameter model can. Nobody fully understands why.

The result: a **single model** (GPT-4, Claude, Gemini) that can do translation, summarization, code generation, question answering, creative writing — without being specifically trained for any of them.

```
   Classical ML:  N tasks → N models → N teams → N years
   GenAI:         N tasks → 1 model → 1 API call → N hours
```

That's the leap. As an engineer, you went from training models to **orchestrating** a foundation model someone else trained. As a Spring engineer, this is gold: **the work is now systems integration, not statistics**. Your 12 years of building distributed systems, designing APIs, handling failures, managing observability — that's exactly what production GenAI needs.

---

## 4. Code example — see the difference yourself

### The "old way" (classical ML — for contrast)

Most classical ML lives in Python/R. For a Java engineer, the equivalent might be a rule engine or hand-tuned logistic regression. Either way, the shape of the work is:

```
1. Collect labeled data (thousands of examples)
2. Design features by hand
3. Train a model on a labeled set
4. Evaluate on a held-out set
5. Deploy
6. Retrain when distribution shifts
```

Time: weeks. Required: labeled data, ML expertise, retraining for each new use case.

### The "GenAI way" (Spring Boot, 2026)

You'll build this fully in this module's mini-project. Preview:

```java
@RestController
@RequestMapping("/api")
public class SentimentController {

    private final RestClient restClient;
    private final String apiKey;

    public SentimentController(
            RestClient.Builder builder,
            @Value("${groq.api-key}") String apiKey) {
        this.restClient = builder
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
        this.apiKey = apiKey;
    }

    @PostMapping("/sentiment")
    public Map<String, String> classify(@RequestBody Map<String, String> body) {
        var text = body.get("text");

        var request = Map.of(
            "model", "llama-3.3-70b-versatile",
            "temperature", 0,
            "messages", List.of(
                Map.of("role", "system",
                       "content", "You are a sentiment classifier. " +
                                  "Reply with one word: POSITIVE, NEGATIVE, NEUTRAL."),
                Map.of("role", "user", "content", text)
            )
        );

        var response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(request)
                .retrieve()
                .body(Map.class);

        // Dig into OpenAI-format response: response.choices[0].message.content
        var choices = (List<Map<String, Object>>) response.get("choices");
        var message = (Map<String, Object>) choices.get(0).get("message");
        var sentiment = ((String) message.get("content")).trim();

        return Map.of("sentiment", sentiment);
    }
}
```

Run it:
```bash
curl -X POST http://localhost:8080/api/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text":"This was amazing"}'
# → {"sentiment":"POSITIVE"}

curl -X POST http://localhost:8080/api/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text":"Mahol thik tha"}'
# → {"sentiment":"NEUTRAL"}    (it understood Hindi too)
```

Time to build: 20 minutes once your environment is set up. Required: a free Groq API key. Works in any language, any domain, with zero training data.

This is the leap. The trade-offs: cost-per-call, latency (200ms–10s), and less control. Classical ML still wins for low-latency hard real-time scenarios, regulated environments needing explainability, and high-volume cases where API costs add up. We'll discuss these trade-offs concretely in Module 2.

### Spring AI preview — same thing, with the framework

In Module 2 the same controller becomes:

```java
@RestController
@RequestMapping("/api")
class SentimentController {

    private final ChatClient chatClient;

    SentimentController(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("You are a sentiment classifier. " +
                          "Reply with one word: POSITIVE, NEGATIVE, NEUTRAL.")
            .build();
    }

    @PostMapping("/sentiment")
    Map<String, String> classify(@RequestBody Map<String, String> body) {
        var result = chatClient.prompt()
            .user(body.get("text"))
            .options(ChatOptions.builder().temperature(0.0).build())
            .call()
            .content();
        return Map.of("sentiment", result.trim());
    }
}
```

15 lines vs. 40. No raw HTTP. Provider swappable via `application.yml`. That's why Spring AI exists. But you needed to feel the raw HTTP first to appreciate it.

---

## 5. Common pitfalls

- **"LLMs make ML obsolete."** Wrong. For structured-data prediction (fraud detection, churn, ranking) classical ML and gradient-boosted trees still beat LLMs by a wide margin. Use the right tool.
- **"GenAI is just deep learning, nothing new."** Technically true, philosophically wrong. The combination of transformer + self-supervised pre-training + scale created qualitative capabilities (in-context learning, instruction following) that simply didn't exist before.
- **"If I can call an API, why understand any of this?"** Production failures, cost surprises, and architecture decisions all require understanding what's underneath. Pilots fly planes without building engines, but they understand thermodynamics.
- **"Spring AI hides the LLM details, so I don't need to know them."** Wrong. Spring AI hides the *plumbing*; the LLM's actual behavior — context windows, hallucinations, cost per token — leaks through every abstraction. Knowing the basics keeps you out of trouble.

---

## 6. When to use what

| Problem | Best tool today | Why |
|---|---|---|
| Spam filter on emails (millions/day) | Classical ML | Cheap, fast, explainable, sufficient |
| Sentiment of customer reviews (low volume, multilingual) | LLM via API | Multilingual, context-aware, zero setup |
| Fraud detection on transactions | Gradient-boosted trees + features | Tabular data, regulators want explainability |
| Image classification, 10 fixed classes | CNN (deep learning) | Mature, well-understood, cheaper than vision-LLM |
| "Find clauses about indemnity in this 200-page contract" | LLM + RAG | Long context, unstructured text |
| Stock price prediction | Classical ML + time series | LLMs are bad at numerical prediction |
| Customer support chatbot | LLM + RAG + Agent | Conversational, integrates many sources |
| Code completion in IDE | LLM (Copilot, Claude Code) | The killer app of GenAI |
| Adding "ask me anything about my Spring app" to your existing service | Spring AI + RAG over your docs | Lives in your existing stack |

Pattern: **structured tabular data → classical ML. Unstructured language/code/image data → GenAI.** Mix when you need both.

---

## 7. Comparison table

| Dimension | Classical ML | Deep Learning (pre-2020) | Generative AI / LLMs |
|---|---|---|---|
| Training data | Labeled, structured | Labeled, often huge | Unlabeled internet text |
| Feature engineering | Manual | Learned by network | Not applicable |
| Reusability across tasks | Low | Low–medium | Very high |
| Compute to train | Low (laptop) | Medium (single GPU) | Massive (thousands of GPUs, months) |
| Compute to use (inference) | Trivial | Low | Medium–high (usually an API call) |
| Explainability | High | Low | Very low |
| Where it shines | Tabular data | Vision, narrow NLP | Open-ended language tasks |
| Your role as engineer | Train + deploy | Train + deploy | **Orchestrate + integrate** |

---

## 8. Production considerations (your Spring engineer hat)

When you ship a GenAI feature into a production Spring Boot system, you bring new concerns:

- **Cost per request.** Every LLM call costs money. A million users × 3 calls/day × ₹0.20/call = ₹6L/month. You need budget tracking. Spring AI exposes token counts via Micrometer — Module 9 covers this.
- **Latency.** A GPT-4 call is 1–10 seconds. Cannot block a synchronous user request. Use streaming (SSE — Module 3), async (`@Async`, Reactor), or background jobs (Kafka — your existing skill).
- **Failure modes.** APIs go down. Rate limits hit. Outputs are non-deterministic. You need retries, fallbacks (try Claude → fall back to Llama on Groq → cached response), timeouts, circuit breakers. **Resilience4j applies here directly** — your existing skills transfer 1:1.
- **Output validation.** LLM might return malformed JSON. Spring AI's `BeanOutputConverter` handles this (Module 4); without it, you write Jackson + retry loops manually.
- **Observability.** You can't read through 10K LLM responses. You need logging of (prompt, response, latency, cost, model_version) per call. Spring AI integrates with Micrometer; Module 9.
- **Privacy.** Sending customer data to OpenAI may violate India's DPDP Act or your customer's contracts. Solutions: local models (Ollama, Module 2), India-region hosting (Azure India, Bedrock India).
- **Prompt versioning.** When you change a prompt, behavior shifts. Treat prompts like code — versioned, code-reviewed, tested. Module 9.

We'll cover each in depth. For now: know they exist.

---

## 9. Interview questions

1. **"Explain to a junior engineer the difference between AI, ML, Deep Learning, and Generative AI."**
   *Good answer:* AI is the umbrella — making machines do intelligent tasks. ML is a subset where the machine learns patterns from data rather than following hand-written rules. Deep learning is a subset of ML using deep neural networks that learn features automatically. Generative AI is a subset of deep learning where the network is large enough, and trained on enough self-supervised data, that it generates novel content rather than just classifying or predicting.

2. **"When would you NOT use an LLM?"**
   *Good answer:* Three scenarios. (1) Structured tabular data with clear features — boosted trees win on cost and accuracy. (2) Hard real-time systems with <100ms predictable latency requirements. (3) Highly regulated environments where decisions must be explainable to auditors — LLMs are black boxes. Also: tasks involving precise numerical math should use code execution or calculator tools, not the LLM directly.

3. **"What changed in 2020–2022 that made GenAI suddenly take off?"**
   *Good answer:* Three things combined. The transformer architecture (2017) gave a parallelizable model that scales well. Self-supervised pre-training on internet-scale text removed the labeling bottleneck. And empirical scaling laws — more parameters + more data + more compute → predictably better and emergently capable models. GPT-3 in 2020 demonstrated the combination; ChatGPT in late 2022 made it accessible.

4. **"What is an emergent capability?"**
   *Good answer:* A capability that appears in larger models but is absent in smaller ones, with no smooth transition. Examples: chain-of-thought reasoning, in-context learning, basic arithmetic. We don't fully understand why these emerge at certain scales. Engineering implication: when picking a model, don't assume "a 7B model will do the same task as a 70B, just slower." Sometimes it can't do it at all.

5. **"What's your role as a Java engineer in this AI era?"**
   *Good answer:* We orchestrate, not train. The economically valuable work has shifted from training models (a few hundred labs in the world) to integrating them well into products (millions of engineers needed). Strong backend systems skills — distributed systems, APIs, observability, resilience — are *more* valuable in this era, not less, because someone has to make these probabilistic, expensive, sometimes-wrong models work reliably in production. Spring AI lets us do that without leaving the Spring ecosystem we already know.

---

## 10. Further reading (free)

- **Andrej Karpathy — "Intro to Large Language Models" (1h video)** — `youtube.com/watch?v=zjkBMFhNj_g` — single best free intro.
- **Stanford CS324 lecture notes — "What is a Foundation Model?"** — `stanford-cs324.github.io/winter2022/lectures/introduction/`
- **Sebastian Raschka — "Understanding Large Language Models" blog** — `sebastianraschka.com/blog`
- **"Attention Is All You Need" (original paper, 2017)** — `arxiv.org/abs/1706.03762` — skim the abstract, intro, Figure 1. You'll return after file 02.
- **Spring AI Blog: Spring AI 1.0 GA announcement** — `spring.io/blog/2025/05/20/spring-ai-1-0-GA-released` — historical context for the Java-side launch.

---

## What's next

Now you know *where* GenAI sits. In file 02 we go inside the box: what is a transformer, and why does it work?

→ Next: `02_transformers_intuition.md`
