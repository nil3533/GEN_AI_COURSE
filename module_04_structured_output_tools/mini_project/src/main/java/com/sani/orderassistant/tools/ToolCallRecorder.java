package com.sani.orderassistant.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sani.orderassistant.dto.ToolCallTrace;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class ToolCallRecorder {

    private final ObjectMapper objectMapper;

    private final ThreadLocal<ToolRunState> current = new ThreadLocal<>();

    public ToolCallRecorder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Scope open(String customerId, boolean cancellationConfirmed, int maxToolCalls) {
        current.set(new ToolRunState(customerId, cancellationConfirmed, maxToolCalls));
        return () -> current.remove();
    }

    public <T> T record(String name, Map<String, Object> arguments, boolean mutating, Supplier<T> supplier) {
        var state = current.get();
        if (state == null) {
            return supplier.get();
        }

        if (state.traces.size() >= state.maxToolCalls) {
            throw new ToolBudgetExceededException("Tool call budget exceeded. Maximum allowed: " + state.maxToolCalls);
        }

        var result = supplier.get();
        state.traces.add(new ToolCallTrace(name, arguments, summarize(result), mutating));
        return result;
    }

    public String customerId() {
        var state = current.get();
        return state == null ? "" : state.customerId;
    }

    public boolean cancellationConfirmed() {
        var state = current.get();
        return state != null && state.cancellationConfirmed;
    }

    public List<ToolCallTrace> snapshot() {
        var state = current.get();
        return state == null ? List.of() : List.copyOf(state.traces);
    }

    private String summarize(Object result) {
        try {
            var json = objectMapper.writeValueAsString(result);
            return json.length() <= 500 ? json : json.substring(0, 500) + "...";
        } catch (JsonProcessingException exception) {
            return String.valueOf(result);
        }
    }

    public interface Scope extends AutoCloseable {

        @Override
        void close();
    }

    public static class ToolBudgetExceededException extends RuntimeException {

        public ToolBudgetExceededException(String message) {
            super(message);
        }
    }

    private static class ToolRunState {

        private final String customerId;

        private final boolean cancellationConfirmed;

        private final int maxToolCalls;

        private final List<ToolCallTrace> traces = new ArrayList<>();

        private ToolRunState(String customerId, boolean cancellationConfirmed, int maxToolCalls) {
            this.customerId = customerId;
            this.cancellationConfirmed = cancellationConfirmed;
            this.maxToolCalls = maxToolCalls;
        }
    }
}
