package com.sani.firstllm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AskRequest(
        @NotBlank(message = "question is required")
        @Size(max = 4000, message = "question must be 4000 characters or less")
        String question
) {
}

