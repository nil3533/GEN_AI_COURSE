package com.sani.ragdocs.dto;

public record EvalCaseResult(
        String question,
        String expectedDocumentId,
        boolean found,
        int rank
) {
}
