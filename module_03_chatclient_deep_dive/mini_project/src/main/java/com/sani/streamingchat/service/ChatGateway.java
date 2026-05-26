package com.sani.streamingchat.service;

import com.sani.streamingchat.config.AiChatProperties;
import reactor.core.publisher.Flux;

public interface ChatGateway {

    AiChatResponse call(AiChatProperties.ProviderSettings settings, PromptContext context, Double temperature);

    Flux<String> stream(AiChatProperties.ProviderSettings settings, PromptContext context, Double temperature);
}
