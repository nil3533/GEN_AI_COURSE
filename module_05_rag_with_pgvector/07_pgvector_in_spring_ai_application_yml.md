# pgvector in application.yml

This module uses Spring profiles:

```text
default profile -> in-memory vector repository for tests
pgvector profile -> PostgreSQL + pgvector
ollama profile -> local Ollama embeddings/chat
```

Typical run:

```powershell
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=pgvector,ollama
```

Keep database connection settings in `application-pgvector.yml`, and keep provider model names configurable through environment variables.
