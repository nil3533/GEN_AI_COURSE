package com.sani.multichat.dto;

import java.util.List;

public record CompareResponse(
        String question,
        int providerCount,
        List<ChatAnswer> results
) {
}
