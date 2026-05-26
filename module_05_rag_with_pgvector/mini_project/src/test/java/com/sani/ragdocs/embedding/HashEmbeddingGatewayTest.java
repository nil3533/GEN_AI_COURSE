package com.sani.ragdocs.embedding;

import com.sani.ragdocs.config.RagProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashEmbeddingGatewayTest {

    @Test
    void returnsStableNormalizedVector() {
        var properties = new RagProperties();
        properties.setEmbeddingDimensions(32);
        var gateway = new HashEmbeddingGateway(properties);

        var first = gateway.embed("Spring AI ChatClient calls chat models");
        var second = gateway.embed("Spring AI ChatClient calls chat models");

        assertThat(first).hasSize(32);
        assertThat(first).containsExactly(second);
        assertThat(magnitude(first)).isCloseTo(1.0, org.assertj.core.data.Offset.offset(0.0001));
    }

    private double magnitude(double[] vector) {
        var sum = 0.0;
        for (double value : vector) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }
}
