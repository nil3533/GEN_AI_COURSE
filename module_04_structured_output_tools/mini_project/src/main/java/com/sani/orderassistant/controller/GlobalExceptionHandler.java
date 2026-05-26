package com.sani.orderassistant.controller;

import com.sani.orderassistant.service.AssistantService;
import com.sani.orderassistant.service.SpringAiAssistantGateway;
import com.sani.orderassistant.tools.ToolCallRecorder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AssistantService.AssistantProviderConfigurationException.class,
            SpringAiAssistantGateway.AssistantProviderConfigurationException.class
    })
    public ResponseEntity<Map<String, String>> providerConfiguration(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(ToolCallRecorder.ToolBudgetExceededException.class)
    public ResponseEntity<Map<String, String>> toolBudget(ToolCallRecorder.ToolBudgetExceededException exception) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException exception) {
        var firstError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("request validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", firstError));
    }
}
