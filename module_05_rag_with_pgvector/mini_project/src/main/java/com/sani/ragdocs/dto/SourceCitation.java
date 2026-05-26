package com.sani.ragdocs.dto;

public record SourceCitation(
        String documentId,
        String title,
        String source,
        int chunkIndex,
        String chunkText,
        double relevanceScore
) {
}
