package com.sani.ragdocs.embedding;

import com.sani.ragdocs.config.RagProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@ConditionalOnProperty(name = "app.rag.embedding-provider", havingValue = "ollama")
public class OllamaEmbeddingGateway implements EmbeddingGateway {

    private final RestClient restClient;

    private final String model;

    public OllamaEmbeddingGateway(RagProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .build();
        this.model = properties.getOllama().getEmbeddingModel();
    }

    @Override
    public double[] embed(String text) {
        var response = restClient.post()
                .uri("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new OllamaEmbeddingRequest(model, text))
                .retrieve()
                .body(OllamaEmbeddingResponse.class);

        if (response == null || CollectionUtils.isEmpty(response.embedding())) {
            throw new EmbeddingProviderException("Ollama returned no embedding.");
        }

        return response.embedding().stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    private record OllamaEmbeddingRequest(String model, String prompt) {
    }

    private record OllamaEmbeddingResponse(List<Double> embedding) {
    }

    public static class EmbeddingProviderException extends RuntimeException {

        public EmbeddingProviderException(String message) {
            super(message);
        }
    }
}
