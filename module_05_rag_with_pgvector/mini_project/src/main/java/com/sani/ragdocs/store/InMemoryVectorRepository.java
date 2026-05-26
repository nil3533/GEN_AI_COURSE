package com.sani.ragdocs.store;

import com.sani.ragdocs.dto.DocumentSummary;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "app.rag.vector-store", havingValue = "memory", matchIfMissing = true)
public class InMemoryVectorRepository implements VectorRepository {

    private final ConcurrentHashMap<String, List<DocumentChunk>> documents = new ConcurrentHashMap<>();

    @Override
    public void saveChunks(List<DocumentChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return;
        }

        documents.put(chunks.getFirst().documentId(), List.copyOf(chunks));
    }

    @Override
    public void deleteDocument(String documentId) {
        documents.remove(documentId);
    }

    @Override
    public void deleteAll() {
        documents.clear();
    }

    @Override
    public List<DocumentSummary> listDocuments() {
        return documents.values().stream()
                .filter(chunks -> !chunks.isEmpty())
                .map(chunks -> {
                    var first = chunks.getFirst();
                    return new DocumentSummary(first.documentId(), first.title(), first.source(), chunks.size());
                })
                .sorted(Comparator.comparing(DocumentSummary::documentId))
                .toList();
    }

    @Override
    public List<RetrievedChunk> search(double[] queryEmbedding, int topK) {
        if (topK <= 0) {
            return List.of();
        }

        return documents.values().stream()
                .flatMap(List::stream)
                .map(chunk -> new RetrievedChunk(
                        chunk.id(),
                        chunk.documentId(),
                        chunk.chunkIndex(),
                        chunk.title(),
                        chunk.source(),
                        chunk.content(),
                        cosineSimilarity(queryEmbedding, chunk.embedding())))
                .sorted(Comparator.comparing(RetrievedChunk::relevanceScore).reversed())
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(double[] left, double[] right) {
        if (left == null || right == null || left.length == 0 || right.length == 0) {
            return 0.0;
        }

        var dot = 0.0;
        var leftMagnitude = 0.0;
        var rightMagnitude = 0.0;
        var length = Math.min(left.length, right.length);
        for (int index = 0; index < length; index++) {
            dot += left[index] * right[index];
            leftMagnitude += left[index] * left[index];
            rightMagnitude += right[index] * right[index];
        }

        if (leftMagnitude == 0.0 || rightMagnitude == 0.0) {
            return 0.0;
        }

        return dot / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }
}
