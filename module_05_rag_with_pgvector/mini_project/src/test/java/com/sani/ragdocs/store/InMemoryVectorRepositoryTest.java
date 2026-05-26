package com.sani.ragdocs.store;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryVectorRepositoryTest {

    @Test
    void searchesByCosineSimilarity() {
        var repository = new InMemoryVectorRepository();
        repository.saveChunks(List.of(
                new DocumentChunk("spring-0", "spring-ai-notes", 0, "Spring AI", "test", "ChatClient calls chat models.", new double[]{1.0, 0.0}),
                new DocumentChunk("pgvector-0", "pgvector-notes", 0, "pgvector", "test", "pgvector stores embeddings.", new double[]{0.0, 1.0})
        ));

        var results = repository.search(new double[]{1.0, 0.0}, 1);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().documentId()).isEqualTo("spring-ai-notes");
        assertThat(results.getFirst().relevanceScore()).isEqualTo(1.0);
    }

    @Test
    void listsDocumentSummaries() {
        var repository = new InMemoryVectorRepository();
        repository.saveChunks(List.of(
                new DocumentChunk("spring-0", "spring-ai-notes", 0, "Spring AI", "test", "Chunk 1", new double[]{1.0, 0.0}),
                new DocumentChunk("spring-1", "spring-ai-notes", 1, "Spring AI", "test", "Chunk 2", new double[]{0.9, 0.1})
        ));

        assertThat(repository.listDocuments())
                .singleElement()
                .satisfies(summary -> {
                    assertThat(summary.documentId()).isEqualTo("spring-ai-notes");
                    assertThat(summary.chunkCount()).isEqualTo(2);
                });
    }
}
