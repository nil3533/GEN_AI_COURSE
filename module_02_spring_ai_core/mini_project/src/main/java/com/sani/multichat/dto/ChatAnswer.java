package com.sani.multichat.dto;

public record ChatAnswer(
        String provider,
        String model,
        String answer,
        long latencyMs,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens,
        String error
) {

    public static ChatAnswer failed(String provider, String model, long latencyMs, String error) {
        return new ChatAnswer(provider, model, null, latencyMs, null, null, null, error);
    }

    public boolean success() {
        return error == null;
    }
}
