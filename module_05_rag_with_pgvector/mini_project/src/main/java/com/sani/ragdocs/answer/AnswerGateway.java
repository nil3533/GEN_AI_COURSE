package com.sani.ragdocs.answer;

import com.sani.ragdocs.store.RetrievedChunk;

import java.util.List;

public interface AnswerGateway {

    String answer(String question, List<RetrievedChunk> context);
}
