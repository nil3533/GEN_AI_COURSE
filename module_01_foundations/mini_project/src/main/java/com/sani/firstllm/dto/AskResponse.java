package com.sani.firstllm.dto;

public record AskResponse(
        String answer,
        int promptTokens,
        int completionTokens,
        long latencyMs,
        String model
) {
}

