# Multi-Source Retrieval

Large systems often retrieve from multiple sources:

- product docs
- tickets
- database rows
- policies
- source code

Keep source metadata on every chunk. Then merge results by score and source priority.

Do not mix sources blindly when permissions differ. Retrieval must respect tenant, user, and data access rules.
