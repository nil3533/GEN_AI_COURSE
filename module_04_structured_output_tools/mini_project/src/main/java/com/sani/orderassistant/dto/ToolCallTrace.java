package com.sani.orderassistant.dto;

import java.util.Map;

public record ToolCallTrace(
        String name,
        Map<String, Object> arguments,
        String result,
        boolean mutating
) {
}
