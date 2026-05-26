package com.sani.ragdocs.store;

import com.sani.ragdocs.config.RagProperties;
import com.sani.ragdocs.dto.DocumentSummary;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "app.rag.vector-store", havingValue = "pgvector")
public class PgVectorRepository implements VectorRepository {

    private static final Logger log = LoggerFactory.getLogger(PgVectorRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final int dimensions;

    public PgVectorRepository(JdbcTemplate jdbcTemplate, RagProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.dimensions = properties.getEmbeddingDimensions();
        if (dimensions < 8) {
            throw new IllegalArgumentException("app.rag.embedding-dimensions must be at least 8.");
        }
    }

    @PostConstruct
    void init() {
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS rag_chunks (
                    id TEXT PRIMARY KEY,
                    document_id TEXT NOT NULL,
                    chunk_index INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    source TEXT NOT NULL,
                    content TEXT NOT NULL,
                    embedding vector(%d) NOT NULL
                )
                """.formatted(dimensions));
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS rag_chunks_document_id_idx ON rag_chunks(document_id)");
        try {
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS rag_chunks_embedding_hnsw_idx ON rag_chunks USING hnsw (embedding vector_cosine_ops)");
        } catch (DataAccessException exception) {
            log.warn("Could not create pgvector HNSW index. Retrieval still works without it: {}", exception.getMessage());
        }
    }

    @Override
    public void saveChunks(List<DocumentChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate("""
                INSERT INTO rag_chunks (id, document_id, chunk_index, title, source, content, embedding)
                VALUES (?, ?, ?, ?, ?, ?, ?::vector)
                ON CONFLICT (id) DO UPDATE SET
                    document_id = EXCLUDED.document_id,
                    chunk_index = EXCLUDED.chunk_index,
                    title = EXCLUDED.title,
                    source = EXCLUDED.source,
                    content = EXCLUDED.content,
                    embedding = EXCLUDED.embedding
                """, chunks, 100, (statement, chunk) -> {
            statement.setString(1, chunk.id());
            statement.setString(2, chunk.documentId());
            statement.setInt(3, chunk.chunkIndex());
            statement.setString(4, chunk.title());
            statement.setString(5, chunk.source());
            statement.setString(6, chunk.content());
            statement.setString(7, toVectorLiteral(chunk.embedding()));
        });
    }

    @Override
    public void deleteDocument(String documentId) {
        jdbcTemplate.update("DELETE FROM rag_chunks WHERE document_id = ?", documentId);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM rag_chunks");
    }

    @Override
    public List<DocumentSummary> listDocuments() {
        return jdbcTemplate.query("""
                SELECT document_id, MAX(title) AS title, MAX(source) AS source, COUNT(*) AS chunk_count
                FROM rag_chunks
                GROUP BY document_id
                ORDER BY document_id
                """, (resultSet, rowNumber) -> new DocumentSummary(
                resultSet.getString("document_id"),
                resultSet.getString("title"),
                resultSet.getString("source"),
                resultSet.getInt("chunk_count")));
    }

    @Override
    public List<RetrievedChunk> search(double[] queryEmbedding, int topK) {
        if (topK <= 0) {
            return List.of();
        }

        var vector = toVectorLiteral(queryEmbedding);
        return jdbcTemplate.query("""
                SELECT id, document_id, chunk_index, title, source, content,
                       GREATEST(0, LEAST(1, 1 - (embedding <=> ?::vector))) AS relevance_score
                FROM rag_chunks
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """, this::toRetrievedChunk, vector, vector, topK);
    }

    private RetrievedChunk toRetrievedChunk(ResultSet resultSet, int rowNumber) throws SQLException {
        return new RetrievedChunk(
                resultSet.getString("id"),
                resultSet.getString("document_id"),
                resultSet.getInt("chunk_index"),
                resultSet.getString("title"),
                resultSet.getString("source"),
                resultSet.getString("content"),
                resultSet.getDouble("relevance_score"));
    }

    private String toVectorLiteral(double[] vector) {
        if (vector == null || vector.length != dimensions) {
            throw new IllegalArgumentException("Embedding vector must have exactly " + dimensions + " dimensions.");
        }

        return Arrays.stream(vector)
                .peek(value -> {
                    if (!Double.isFinite(value)) {
                        throw new IllegalArgumentException("Embedding vector contains a non-finite value.");
                    }
                })
                .mapToObj(Double::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
