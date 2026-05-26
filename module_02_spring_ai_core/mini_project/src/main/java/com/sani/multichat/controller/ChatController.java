package com.sani.multichat.controller;

import com.sani.multichat.dto.AskRequest;
import com.sani.multichat.dto.ChatAnswer;
import com.sani.multichat.dto.CompareResponse;
import com.sani.multichat.service.AiChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/ask")
    public ChatAnswer ask(@Valid @RequestBody AskRequest request) {
        return aiChatService.ask(request.question());
    }

    @PostMapping("/compare")
    public CompareResponse compare(@Valid @RequestBody AskRequest request) {
        return aiChatService.compare(request.question());
    }

    @GetMapping("/providers")
    public Map<String, Object> providers() {
        return aiChatService.providerSummary();
    }
}
