# Contributing

This repository is a Spring AI learning path for Java and Spring Boot developers. Contributions should keep the course runnable, focused, and easy to learn from.

## Scope

Good contributions:

- improve module explanations
- add diagrams or screenshots that clarify the learning path
- fix runnable Spring Boot mini-projects
- add deterministic tests
- improve setup instructions
- document provider-specific behavior

Avoid:

- adding Python tooling unless a module explicitly needs a comparison
- calling paid live LLM APIs from default tests
- committing secrets or `.env` files
- broad rewrites that change module structure without a clear benefit

## Module Structure

Module folders use:

```text
module_XX_descriptive_slug/
```

Runnable Spring Boot work usually lives in:

```text
module_XX_descriptive_slug/mini_project/
```

Mini-projects should use Maven layout:

```text
src/main/java/
src/main/resources/
src/test/java/
```

## Documentation Style

Keep prose practical and concise. Every module should answer:

- why the topic matters
- what the learner should build
- how to run it locally
- what to test
- what interview questions it prepares for

Prefer Java and Spring examples. Keep provider credentials in environment variables.

## Testing

For a mini-project, run:

```powershell
mvn test
```

Default tests should use mocks, local fixtures, or pure service tests. Live model calls should require an explicit profile or manual smoke test.

## Pull Request Checklist

Before opening a PR:

- docs link to the right module paths
- `mvn test` passes for changed mini-projects
- no secrets or local `.env` files are included
- generated `target/`, `node_modules/`, and logs are not committed
- new APIs include curl examples
- new diagrams are referenced from Markdown

## Security

Never commit API keys, provider tokens, private certificates, or generated credentials. Use placeholders in documentation and environment variables in code.
