package com.sani.streamingchat.controller;

import com.sani.streamingchat.dto.ChatReply;
import com.sani.streamingchat.dto.ChatRequest;
import com.sani.streamingchat.dto.ChatStreamEvent;
import com.sani.streamingchat.dto.SessionSummary;
import com.sani.streamingchat.service.ChatConversationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatConversationService chatConversationService;

    public ChatController(ChatConversationService chatConversationService) {
        this.chatConversationService = chatConversationService;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatStreamEvent>> stream(@Valid @RequestBody ChatRequest request) {
        return chatConversationService.stream(request)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/non-streaming")
    public Mono<ChatReply> nonStreaming(@Valid @RequestBody ChatRequest request) {
        return Mono.fromCallable(() -> chatConversationService.nonStreaming(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/providers")
    public Map<String, Object> providers() {
        return chatConversationService.providerSummary();
    }

    @GetMapping("/sessions/{sessionId}")
    public SessionSummary session(@PathVariable String sessionId) {
        return chatConversationService.sessionSummary(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public Map<String, String> clearSession(@PathVariable String sessionId) {
        chatConversationService.clearSession(sessionId);
        return Map.of("status", "cleared", "sessionId", sessionId);
    }
}
