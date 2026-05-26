package com.sani.orderassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AssistantRequest(
        @NotBlank(message = "customerId is required")
        @Size(max = 80, message = "customerId must be 80 characters or less")
        String customerId,

        @NotBlank(message = "message is required")
        @Size(max = 2000, message = "message must be 2000 characters or less")
        String message,

        boolean confirmed
) {
}
