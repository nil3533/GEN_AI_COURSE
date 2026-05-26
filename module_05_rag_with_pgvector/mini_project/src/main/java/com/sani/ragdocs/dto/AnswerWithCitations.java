package com.sani.ragdocs.dto;

import java.util.List;

public record AnswerWithCitations(
        String question,
        String answer,
        List<SourceCitation> sources
) {

    public AnswerWithCitations {
        sources = sources == null ? List.of() : List.copyOf(sources);
    }
}
