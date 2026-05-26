package com.sani.streamingchat.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @Size(max = 100, message = "sessionId must be 100 characters or less")
        String sessionId,

        @NotBlank(message = "message is required")
        @Size(max = 4000, message = "message must be 4000 characters or less")
        String message,

        @DecimalMin(value = "0.0", message = "temperature must be at least 0.0")
        @DecimalMax(value = "2.0", message = "temperature must be at most 2.0")
        Double temperature
) {
}
