package com.sani.ragdocs.store;

public record RetrievedChunk(
        String id,
        String documentId,
        int chunkIndex,
        String title,
        String source,
        String content,
        double relevanceScore
) {
}
