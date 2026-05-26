package com.sani.streamingchat.service;

public record AiChatResponse(
        String answer,
        String model,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
) {
}
