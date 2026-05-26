package com.sani.ragdocs.dto;

public record IngestDocumentResponse(
        String documentId,
        String title,
        int chunksStored
) {
}
