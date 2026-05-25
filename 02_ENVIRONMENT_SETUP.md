# One-Time Environment Setup

> **Do this once before Module 1. ~45 minutes.** Everything in the course assumes this is in place.

---

## What you're installing

| Tool | Why | Version |
|---|---|---|
| **Java 21** | Spring Boot 4.x needs 17+; 21 is the LTS and Spring AI 2.0 sweet spot | OpenJDK 21 |
| **Maven** | Build tool. Gradle alternatives noted but Maven is the default in Spring AI docs | 3.9+ |
| **Docker Desktop** | For PostgreSQL+pgvector and Ollama containers | latest |
| **PostgreSQL + pgvector** | Vector store for RAG (Module 5 onward) | postgres:16, pgvector extension |
| **Ollama** | Run LLMs locally, free forever | latest |
| **Node.js + npm** | Only for React frontends in Module 10 | Node 20 LTS |
| **IDE** | Pick one | IntelliJ IDEA Community (free) or VS Code + Java/Spring extensions |
| **Git** | Obvious | latest |

---

## 1. Java 21 (10 min)

```bash
# Check what you have
java -version

# Install via SDKMAN (recommended — easy version switching)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.5-tem    # Eclipse Temurin LTS
sdk default java 21.0.5-tem

# Verify
java -version    # should say "21.x"
```

**Windows:** download Temurin 21 from adoptium.net and add to PATH. SDKMAN also works in WSL2.

---

## 2. Maven (5 min)

```bash
sdk install maven 3.9.9
mvn -version
```

Or use the Maven wrapper (`mvnw`) that Spring Boot projects ship with — that requires no global install.

---

## 3. Docker Desktop (10 min)

Install from docker.com/products/docker-desktop. Verify:

```bash
docker --version
docker compose version
```

You'll use Docker Compose throughout the course to spin up Postgres+pgvector and Ollama as needed.

---

## 4. PostgreSQL + pgvector (5 min — just pull the image)

We'll use the official `pgvector/pgvector` image. Pull it now so it's cached:

```bash
docker pull pgvector/pgvector:pg16
```

We won't run it yet — each project's `docker-compose.yml` will start it. But pre-pulling saves time later.

A reusable Compose file you'll reference in many modules:

```yaml
# docker-compose.yml — Postgres + pgvector for any Spring AI project
services:
  postgres:
    image: pgvector/pgvector:pg16
    container_name: spring-ai-postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres-data:
```

Save it somewhere central — you'll copy it into many module folders.

---

## 5. Ollama (10 min — download + first model)

```bash
# macOS/Linux
curl -fsSL https://ollama.com/install.sh | sh

# Windows: download from ollama.com

# Pull a starter model — Llama 3.1 8B fits on most laptops (5GB)
ollama pull llama3.1:8b

# Also pull an embedding model (we'll use this for RAG in Module 5)
ollama pull nomic-embed-text

# Sanity check — should chat in your terminal
ollama run llama3.1:8b
# Type a message, get a reply. /bye to exit.
```

Ollama exposes a REST API on `http://localhost:11434` — Spring AI talks to it directly.

**If your laptop has limited RAM (<16GB):** use `llama3.2:3b` instead of `llama3.1:8b`. Quality is lower but it fits in ~3GB.

---

## 6. Get free API keys (10 min)

We'll use these throughout. Get them now so you don't break flow later.

### Groq (recommended primary — free, very fast)

1. Go to console.groq.com, sign up with Google
2. API Keys → Create API Key → copy

### Google AI Studio (Gemini — free tier)

1. Go to ai.google.dev, sign in
2. "Get API key" → create new project → create API key → copy

### (Optional) OpenAI

1. platform.openai.com, sign up
2. Settings → API keys → create new secret key
3. Add ₹500 credits (covers all course usage with room to spare)

### (Optional) Anthropic Claude

1. console.anthropic.com, sign up
2. Settings → API keys → create key
3. Pay-as-you-go, ₹500 credit covers a lot

---

## 7. Store credentials safely

**Never commit API keys to git.** Use environment variables.

Create `~/.genai-env` (or `%USERPROFILE%\.genai-env.ps1` on Windows):

```bash
# ~/.genai-env  (Linux/macOS)
export GROQ_API_KEY="your_groq_key_here"
export GOOGLE_API_KEY="your_gemini_key_here"
export OPENAI_API_KEY="your_openai_key_here"
export ANTHROPIC_API_KEY="your_anthropic_key_here"
```

Source it in every terminal:

```bash
echo 'source ~/.genai-env' >> ~/.zshrc    # or ~/.bashrc
source ~/.zshrc
```

Verify:

```bash
echo $GROQ_API_KEY    # should print your key
```

Spring Boot reads `${GROQ_API_KEY}` from `application.yml` natively, so you'll write:

```yaml
spring:
  ai:
    openai:
      api-key: ${GROQ_API_KEY}
      base-url: https://api.groq.com/openai/v1
```

---

## 8. IDE setup (5 min)

### IntelliJ IDEA Community Edition (recommended)

Download from jetbrains.com/idea/download. Install plugins (Settings → Plugins):
- Spring Boot (bundled — verify it's enabled)
- Lombok (if you plan to use it)
- Maven (bundled)

### VS Code (alternative)

Install the **Extension Pack for Java** from Microsoft, plus **Spring Boot Extension Pack** from VMware.

---

## 9. Verify the whole stack (5 min)

Create a fresh Spring Boot project to confirm everything works:

```bash
# Use Spring Initializr via CLI (or visit start.spring.io)
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.5.0 \
  -d javaVersion=21 \
  -d groupId=com.sani \
  -d artifactId=verify-setup \
  -d packageName=com.sani.verify \
  -d dependencies=web,actuator \
  -o verify-setup.zip

unzip verify-setup.zip -d verify-setup
cd verify-setup
./mvnw spring-boot:run
```

Open `http://localhost:8080/actuator/health` — should return `{"status":"UP"}`. If yes, you're set.

---

## 10. Create your GitHub portfolio repo

```bash
mkdir -p ~/code/sani-genai-journey
cd ~/code/sani-genai-journey
git init
echo "# Sani's GenAI Journey — Spring AI Edition" > README.md
echo ".env" > .gitignore
echo "target/" >> .gitignore
echo "*.iml" >> .gitignore
echo ".idea/" >> .gitignore
echo "node_modules/" >> .gitignore
git add .
git commit -m "Initialize learning repo"

# Push to GitHub (create the repo on github.com first)
git remote add origin git@github.com:YOUR_USERNAME/sani-genai-journey.git
git push -u origin main
```

**This repo is your portfolio.** Every module's project goes here. By Module 13 you'll have a repo recruiters will actually click on.

---

## Sanity checklist

Before starting Module 1, confirm:

- [ ] `java -version` says 21
- [ ] `mvn -version` works
- [ ] `docker compose version` works
- [ ] `docker pull pgvector/pgvector:pg16` completed
- [ ] `ollama run llama3.1:8b` works and you got a reply
- [ ] `echo $GROQ_API_KEY` prints your key
- [ ] `~/code/sani-genai-journey` exists and is pushed to GitHub

If all checked, → open `module_01_foundations/README.md`.
