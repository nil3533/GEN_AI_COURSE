package com.sani.firstllm.controller;

import com.sani.firstllm.dto.AskRequest;
import com.sani.firstllm.dto.AskResponse;
import com.sani.firstllm.service.LlmService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

    private final LlmService llmService;

    public AskController(LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return llmService.ask(request.question());
    }
}

