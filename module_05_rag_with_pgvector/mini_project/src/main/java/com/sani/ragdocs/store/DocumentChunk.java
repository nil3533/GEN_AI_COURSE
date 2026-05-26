package com.sani.ragdocs.store;

public record DocumentChunk(
        String id,
        String documentId,
        int chunkIndex,
        String title,
        String source,
        String content,
        double[] embedding
) {
}
