package com.sani.streamingchat.service;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public record PromptContext(
        String sessionId,
        String userMessage,
        String systemPromptTemplate,
        String persona,
        String currentDate,
        List<Message> history
) {
}
