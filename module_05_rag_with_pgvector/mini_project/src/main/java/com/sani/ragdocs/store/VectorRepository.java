package com.sani.ragdocs.store;

import com.sani.ragdocs.dto.DocumentSummary;

import java.util.List;

public interface VectorRepository {

    void saveChunks(List<DocumentChunk> chunks);

    void deleteDocument(String documentId);

    void deleteAll();

    List<DocumentSummary> listDocuments();

    List<RetrievedChunk> search(double[] queryEmbedding, int topK);
}
