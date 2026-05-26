package com.sani.ragdocs.controller;

import com.sani.ragdocs.dto.AnswerWithCitations;
import com.sani.ragdocs.dto.AskRequest;
import com.sani.ragdocs.dto.DocumentSummary;
import com.sani.ragdocs.dto.EvalResponse;
import com.sani.ragdocs.dto.IngestDocumentRequest;
import com.sani.ragdocs.dto.IngestDocumentResponse;
import com.sani.ragdocs.service.RagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/documents/ingest")
    public IngestDocumentResponse ingest(@Valid @RequestBody IngestDocumentRequest request) {
        return ragService.ingest(request);
    }

    @GetMapping("/documents")
    public List<DocumentSummary> documents() {
        return ragService.listDocuments();
    }

    @DeleteMapping("/documents")
    public ResponseEntity<Void> deleteDocuments() {
        ragService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/rag/ask")
    public AnswerWithCitations ask(@Valid @RequestBody AskRequest request) {
        return ragService.ask(request);
    }

    @PostMapping("/rag/eval")
    public EvalResponse evaluate() {
        return ragService.evaluate();
    }
}
