package com.sani.streamingchat.dto;

public record ChatReply(
        String sessionId,
        String provider,
        String model,
        String answer,
        long latencyMs,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
) {
}
