package com.sani.streamingchat.controller;

import com.sani.streamingchat.service.ChatConversationService;
import com.sani.streamingchat.service.SpringAiChatGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ChatConversationService.ChatProviderConfigurationException.class,
            SpringAiChatGateway.ChatProviderConfigurationException.class
    })
    public ResponseEntity<Map<String, String>> providerConfiguration(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(SpringAiChatGateway.ChatProviderCallException.class)
    public ResponseEntity<Map<String, String>> providerCall(SpringAiChatGateway.ChatProviderCallException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> validation(WebExchangeBindException exception) {
        var firstError = exception.getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("request validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", firstError));
    }
}
