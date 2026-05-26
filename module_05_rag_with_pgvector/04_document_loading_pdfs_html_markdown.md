# Document Loading: PDFs, HTML, and Markdown

For this module, start with text and Markdown because they are easy to inspect and debug.

Production systems add readers for:

- PDFs
- HTML pages
- Markdown docs
- database rows
- support tickets
- source code

Always preserve metadata:

```text
documentId
title
source
chunkIndex
```

Citations are only useful when every retrieved chunk carries source metadata.
