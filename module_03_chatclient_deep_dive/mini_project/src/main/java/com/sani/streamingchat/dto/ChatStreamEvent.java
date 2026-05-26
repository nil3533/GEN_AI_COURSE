package com.sani.streamingchat.dto;

public record ChatStreamEvent(
        String type,
        String sessionId,
        String content,
        String provider,
        String model,
        Long latencyMs,
        String error
) {

    public static ChatStreamEvent start(String sessionId, String provider, String model) {
        return new ChatStreamEvent("start", sessionId, "", provider, model, null, null);
    }

    public static ChatStreamEvent token(String sessionId, String provider, String model, String content) {
        return new ChatStreamEvent("token", sessionId, content, provider, model, null, null);
    }

    public static ChatStreamEvent done(String sessionId, String provider, String model, long latencyMs) {
        return new ChatStreamEvent("done", sessionId, "", provider, model, latencyMs, null);
    }

    public static ChatStreamEvent error(String sessionId, String provider, String model, long latencyMs, String error) {
        return new ChatStreamEvent("error", sessionId, "", provider, model, latencyMs, error);
    }
}
