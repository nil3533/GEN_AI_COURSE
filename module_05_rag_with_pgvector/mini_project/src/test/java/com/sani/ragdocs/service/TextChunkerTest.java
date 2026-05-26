package com.sani.ragdocs.service;

import com.sani.ragdocs.config.RagProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextChunkerTest {

    @Test
    void splitsLongTextWithBoundedChunks() {
        var properties = new RagProperties();
        properties.setChunkSize(80);
        properties.setChunkOverlap(10);
        var chunker = new TextChunker(properties);

        var chunks = chunker.split("""
                Spring AI wraps provider APIs behind ChatClient. The application decides what context should be sent.
                Retrieval augmented generation adds document chunks to the prompt. pgvector stores embeddings in PostgreSQL.
                """);

        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks).allSatisfy(chunk -> assertThat(chunk).hasSizeLessThanOrEqualTo(80));
    }

    @Test
    void rejectsOverlapGreaterThanChunkSize() {
        var properties = new RagProperties();
        properties.setChunkSize(20);
        properties.setChunkOverlap(20);
        var chunker = new TextChunker(properties);

        assertThatThrownBy(() -> chunker.split("This content is valid but the configuration is not."))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("chunk-overlap");
    }
}
