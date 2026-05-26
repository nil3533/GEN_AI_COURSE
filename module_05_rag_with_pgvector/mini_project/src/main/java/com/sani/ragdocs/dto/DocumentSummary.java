package com.sani.ragdocs.dto;

public record DocumentSummary(
        String documentId,
        String title,
        String source,
        int chunkCount
) {
}
