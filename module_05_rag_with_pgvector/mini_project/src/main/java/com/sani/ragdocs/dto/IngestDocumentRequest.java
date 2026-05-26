package com.sani.ragdocs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IngestDocumentRequest(
        @NotBlank(message = "documentId is required")
        @Size(max = 120, message = "documentId must be 120 characters or less")
        String documentId,

        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be 200 characters or less")
        String title,

        @NotBlank(message = "source is required")
        @Size(max = 300, message = "source must be 300 characters or less")
        String source,

        @NotBlank(message = "content is required")
        String content
) {
}
