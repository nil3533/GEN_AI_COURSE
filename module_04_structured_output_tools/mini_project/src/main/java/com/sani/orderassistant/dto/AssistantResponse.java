package com.sani.orderassistant.dto;

import java.util.List;

public record AssistantResponse(
        AssistantIntent intent,
        String answer,
        boolean actionRequired,
        List<ToolCallTrace> toolsCalled,
        List<String> safetyNotes,
        double confidence
) {

    public AssistantResponse {
        intent = intent == null ? AssistantIntent.UNKNOWN : intent;
        answer = answer == null ? "" : answer;
        toolsCalled = toolsCalled == null ? List.of() : List.copyOf(toolsCalled);
        safetyNotes = safetyNotes == null ? List.of() : List.copyOf(safetyNotes);
    }

    public AssistantResponse withToolTraces(List<ToolCallTrace> traces) {
        return new AssistantResponse(intent, answer, actionRequired, traces, safetyNotes, confidence);
    }

    public AssistantResponse withSafetyNote(String note) {
        if (note == null || note.isBlank()) {
            return this;
        }
        var notes = new java.util.ArrayList<>(safetyNotes);
        notes.add(note);
        return new AssistantResponse(intent, answer, actionRequired, toolsCalled, notes, confidence);
    }

    public static AssistantResponse fallback(String answer) {
        return new AssistantResponse(AssistantIntent.UNKNOWN, answer, true, List.of(), List.of(), 0.0);
    }
}
