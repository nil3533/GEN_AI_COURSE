package com.sani.ragdocs.service;

import com.sani.ragdocs.answer.TemplateAnswerGateway;
import com.sani.ragdocs.config.RagProperties;
import com.sani.ragdocs.dto.AskRequest;
import com.sani.ragdocs.dto.IngestDocumentRequest;
import com.sani.ragdocs.embedding.EmbeddingGateway;
import com.sani.ragdocs.store.InMemoryVectorRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RagServiceTest {

    @Test
    void ingestsDocumentsAndAnswersWithCitations() {
        var service = newService();

        var response = service.ingest(new IngestDocumentRequest(
                "spring-ai-notes",
                "Spring AI Notes",
                "unit-test",
                "Spring AI ChatClient is used for calling chat models through a fluent API."));

        var answer = service.ask(new AskRequest("What is ChatClient used for?", 3));

        assertThat(response.chunksStored()).isEqualTo(1);
        assertThat(answer.answer()).contains("ChatClient");
        assertThat(answer.sources())
                .singleElement()
                .satisfies(source -> assertThat(source.documentId()).isEqualTo("spring-ai-notes"));
    }

    @Test
    void evaluatesRetrievalAgainstFixedCases() {
        var service = newService();
        service.ingest(new IngestDocumentRequest(
                "spring-ai-notes",
                "Spring AI Notes",
                "unit-test",
                "Spring AI ChatClient is the fluent API for chat model calls."));
        service.ingest(new IngestDocumentRequest(
                "pgvector-notes",
                "pgvector Notes",
                "unit-test",
                "pgvector stores embeddings in PostgreSQL for similarity search."));
        service.ingest(new IngestDocumentRequest(
                "rag-notes",
                "RAG Notes",
                "unit-test",
                "RAG passes retrieved chunks into the prompt so answers stay grounded."));

        var result = service.evaluate();

        assertThat(result.totalCases()).isEqualTo(3);
        assertThat(result.passed()).isGreaterThanOrEqualTo(2);
    }

    private RagService newService() {
        var properties = new RagProperties();
        properties.setChunkSize(500);
        properties.setChunkOverlap(50);
        properties.setDefaultTopK(3);
        return new RagService(
                new TextChunker(properties),
                new KeywordEmbeddingGateway(),
                new InMemoryVectorRepository(),
                new TemplateAnswerGateway(),
                properties);
    }

    private static class KeywordEmbeddingGateway implements EmbeddingGateway {

        @Override
        public double[] embed(String text) {
            var lower = text.toLowerCase();
            if (lower.contains("pgvector") || lower.contains("embedding") || lower.contains("similarity")) {
                return new double[]{0.0, 1.0, 0.0};
            }
            if (lower.contains("rag") || lower.contains("retrieved") || lower.contains("grounded")) {
                return new double[]{0.0, 0.0, 1.0};
            }
            return new double[]{1.0, 0.0, 0.0};
        }
    }
}
