package com.sani.multichat.service;

import com.sani.multichat.config.AiProviderProperties;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiChatServiceTest {

    @Test
    void askFailsWhenActiveProviderIsUnknown() {
        var properties = new AiProviderProperties();
        properties.setActiveProvider("missing");

        var service = new AiChatService(properties);

        assertThatThrownBy(() -> service.ask("hello"))
                .isInstanceOf(AiChatService.ProviderConfigurationException.class)
                .hasMessageContaining("Unknown provider");
    }

    @Test
    void askFailsWhenActiveProviderIsDisabled() {
        var properties = new AiProviderProperties();
        properties.setActiveProvider("groq");
        properties.getProviders().put("groq", provider("openai-compatible", false));

        var service = new AiChatService(properties);

        assertThatThrownBy(() -> service.ask("hello"))
                .isInstanceOf(AiChatService.ProviderConfigurationException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    void compareReturnsPerProviderFailureInsteadOfFailingWholeRequest() {
        var properties = new AiProviderProperties();
        properties.setActiveProvider("groq");
        properties.setProviders(new LinkedHashMap<>());
        properties.getProviders().put("groq", provider("openai-compatible", true));

        var service = new AiChatService(properties);

        var response = service.compare("hello");

        assertThat(response.providerCount()).isEqualTo(1);
        assertThat(response.results()).hasSize(1);
        assertThat(response.results().getFirst().provider()).isEqualTo("groq");
        assertThat(response.results().getFirst().success()).isFalse();
        assertThat(response.results().getFirst().error()).contains("requires an API key");
    }

    private AiProviderProperties.ProviderSettings provider(String type, boolean enabled) {
        var provider = new AiProviderProperties.ProviderSettings();
        provider.setEnabled(enabled);
        provider.setType(type);
        provider.setBaseUrl("https://example.test");
        provider.setModel("test-model");
        provider.setTemperature(0.2);
        provider.setMaxTokens(100);
        return provider;
    }
}
