package com.sani.ragdocs.dto;

import java.util.List;

public record EvalResponse(
        int totalCases,
        int passed,
        double recallAtK,
        List<EvalCaseResult> results
) {
}
