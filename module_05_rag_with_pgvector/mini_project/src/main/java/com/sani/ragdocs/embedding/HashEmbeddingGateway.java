package com.sani.ragdocs.embedding;

import com.sani.ragdocs.config.RagProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Component
@ConditionalOnProperty(name = "app.rag.embedding-provider", havingValue = "hash", matchIfMissing = true)
public class HashEmbeddingGateway implements EmbeddingGateway {

    private final int dimensions;

    public HashEmbeddingGateway(RagProperties properties) {
        this.dimensions = properties.getEmbeddingDimensions();
        if (dimensions < 8) {
            throw new IllegalArgumentException("app.rag.embedding-dimensions must be at least 8.");
        }
    }

    @Override
    public double[] embed(String text) {
        var vector = new double[dimensions];
        if (!StringUtils.hasText(text)) {
            return vector;
        }

        var normalized = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();

        if (!StringUtils.hasText(normalized)) {
            return vector;
        }

        for (String token : normalized.split("\\s+")) {
            var hash = token.hashCode();
            var index = Math.floorMod(hash, dimensions);
            var sign = Math.floorMod(hash * 31, 2) == 0 ? 1.0 : -1.0;
            var weight = 1.0 + Math.min(token.length(), 16) / 16.0;
            vector[index] += sign * weight;
        }

        normalize(vector);
        return vector;
    }

    private void normalize(double[] vector) {
        var magnitude = 0.0;
        for (double value : vector) {
            magnitude += value * value;
        }

        if (magnitude == 0.0) {
            return;
        }

        var divisor = Math.sqrt(magnitude);
        for (int index = 0; index < vector.length; index++) {
            vector[index] = vector[index] / divisor;
        }
    }
}
