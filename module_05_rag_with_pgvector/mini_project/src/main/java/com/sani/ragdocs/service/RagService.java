package com.sani.ragdocs.service;

import com.sani.ragdocs.answer.AnswerGateway;
import com.sani.ragdocs.config.RagProperties;
import com.sani.ragdocs.dto.AnswerWithCitations;
import com.sani.ragdocs.dto.AskRequest;
import com.sani.ragdocs.dto.DocumentSummary;
import com.sani.ragdocs.dto.EvalCaseResult;
import com.sani.ragdocs.dto.EvalResponse;
import com.sani.ragdocs.dto.IngestDocumentRequest;
import com.sani.ragdocs.dto.IngestDocumentResponse;
import com.sani.ragdocs.dto.SourceCitation;
import com.sani.ragdocs.embedding.EmbeddingGateway;
import com.sani.ragdocs.store.DocumentChunk;
import com.sani.ragdocs.store.RetrievedChunk;
import com.sani.ragdocs.store.VectorRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class RagService {

    private static final List<EvalCase> EVAL_CASES = List.of(
            new EvalCase("What API does Spring AI use for chat model calls?", "spring-ai-notes"),
            new EvalCase("Where are embeddings stored for similarity search?", "pgvector-notes"),
            new EvalCase("Why does RAG pass retrieved chunks into the prompt?", "rag-notes")
    );

    private final TextChunker textChunker;

    private final EmbeddingGateway embeddingGateway;

    private final VectorRepository vectorRepository;

    private final AnswerGateway answerGateway;

    private final RagProperties properties;

    public RagService(
            TextChunker textChunker,
            EmbeddingGateway embeddingGateway,
            VectorRepository vectorRepository,
            AnswerGateway answerGateway,
            RagProperties properties) {
        this.textChunker = textChunker;
        this.embeddingGateway = embeddingGateway;
        this.vectorRepository = vectorRepository;
        this.answerGateway = answerGateway;
        this.properties = properties;
    }

    public IngestDocumentResponse ingest(IngestDocumentRequest request) {
        var chunks = textChunker.split(request.content());
        var documentChunks = IntStream.range(0, chunks.size())
                .mapToObj(index -> toDocumentChunk(request, chunks.get(index), index))
                .toList();

        vectorRepository.deleteDocument(request.documentId());
        vectorRepository.saveChunks(documentChunks);

        return new IngestDocumentResponse(request.documentId(), request.title(), documentChunks.size());
    }

    public List<DocumentSummary> listDocuments() {
        return vectorRepository.listDocuments();
    }

    public void deleteAll() {
        vectorRepository.deleteAll();
    }

    public AnswerWithCitations ask(AskRequest request) {
        var topK = request.topK() == null ? properties.getDefaultTopK() : request.topK();
        var queryEmbedding = embeddingGateway.embed(request.question());
        var retrievedChunks = vectorRepository.search(queryEmbedding, topK);
        var answer = answerGateway.answer(request.question(), retrievedChunks);
        var citations = retrievedChunks.stream()
                .map(this::toCitation)
                .toList();

        return new AnswerWithCitations(request.question(), answer, citations);
    }

    public EvalResponse evaluate() {
        var topK = properties.getDefaultTopK();
        var results = EVAL_CASES.stream()
                .map(evalCase -> evaluateCase(evalCase, topK))
                .toList();
        var passed = (int) results.stream().filter(EvalCaseResult::found).count();
        var recallAtK = results.isEmpty() ? 0.0 : passed / (double) results.size();

        return new EvalResponse(results.size(), passed, recallAtK, results);
    }

    private DocumentChunk toDocumentChunk(IngestDocumentRequest request, String content, int index) {
        var stableIdInput = "%s:%d:%s".formatted(request.documentId(), index, content);
        return new DocumentChunk(
                "%s-%s".formatted(request.documentId(), UUID.nameUUIDFromBytes(stableIdInput.getBytes(StandardCharsets.UTF_8))),
                request.documentId(),
                index,
                request.title(),
                request.source(),
                content,
                embeddingGateway.embed(content)
        );
    }

    private SourceCitation toCitation(RetrievedChunk chunk) {
        return new SourceCitation(
                chunk.documentId(),
                chunk.title(),
                chunk.source(),
                chunk.chunkIndex(),
                excerpt(chunk.content()),
                roundedScore(chunk.relevanceScore())
        );
    }

    private String excerpt(String content) {
        if (content.length() <= 500) {
            return content;
        }
        return content.substring(0, 497).trim() + "...";
    }

    private double roundedScore(double score) {
        return Math.round(score * 1000.0) / 1000.0;
    }

    private EvalCaseResult evaluateCase(EvalCase evalCase, int topK) {
        var queryEmbedding = embeddingGateway.embed(evalCase.question());
        var retrievedChunks = vectorRepository.search(queryEmbedding, topK);
        var rank = -1;
        for (int index = 0; index < retrievedChunks.size(); index++) {
            if (retrievedChunks.get(index).documentId().equals(evalCase.expectedDocumentId())) {
                rank = index + 1;
                break;
            }
        }

        return new EvalCaseResult(evalCase.question(), evalCase.expectedDocumentId(), rank > 0, rank);
    }

    private record EvalCase(String question, String expectedDocumentId) {
    }
}
