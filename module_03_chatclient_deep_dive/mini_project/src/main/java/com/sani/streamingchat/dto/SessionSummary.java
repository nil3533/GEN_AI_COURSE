package com.sani.streamingchat.dto;

public record SessionSummary(
        String sessionId,
        int messageCount,
        int maxHistoryMessages
) {
}
