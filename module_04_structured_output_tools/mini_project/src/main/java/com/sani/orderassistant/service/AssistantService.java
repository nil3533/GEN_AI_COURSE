package com.sani.orderassistant.service;

import com.sani.orderassistant.config.AiAssistantProperties;
import com.sani.orderassistant.dto.AssistantRequest;
import com.sani.orderassistant.dto.AssistantResponse;
import com.sani.orderassistant.tools.ToolCallRecorder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AssistantService {

    private final AiAssistantProperties properties;

    private final AssistantGateway assistantGateway;

    private final ToolCallRecorder toolCallRecorder;

    public AssistantService(
            AiAssistantProperties properties,
            AssistantGateway assistantGateway,
            ToolCallRecorder toolCallRecorder) {
        this.properties = properties;
        this.assistantGateway = assistantGateway;
        this.toolCallRecorder = toolCallRecorder;
    }

    public AssistantResponse respond(AssistantRequest request) {
        var settings = activeProviderSettings();

        try (var ignored = toolCallRecorder.open(request.customerId(), request.confirmed(), properties.getMaxToolCalls())) {
            var response = assistantGateway.generate(request, settings);
            var traced = response.withToolTraces(toolCallRecorder.snapshot());

            if (!request.confirmed() && includesMutatingTool(traced)) {
                return traced.withSafetyNote("Mutating tool calls require explicit confirmation.");
            }
            return traced;
        }
    }

    public Map<String, Object> providerSummary() {
        var providers = new LinkedHashMap<String, Map<String, Object>>();
        properties.getProviders().forEach((name, settings) -> providers.put(name, Map.of(
                "enabled", settings.isEnabled(),
                "type", nullToEmpty(settings.getType()),
                "model", nullToEmpty(settings.getModel()),
                "baseUrl", nullToEmpty(settings.getBaseUrl()),
                "requiresApiKey", settings.requiresApiKey(),
                "apiKeyConfigured", StringUtils.hasText(settings.getApiKey())
        )));

        return Map.of(
                "activeProvider", properties.getActiveProvider(),
                "maxToolCalls", properties.getMaxToolCalls(),
                "providers", providers
        );
    }

    private AiAssistantProperties.ProviderSettings activeProviderSettings() {
        var providerName = properties.getActiveProvider();
        var settings = properties.getProviders().get(providerName);
        if (settings == null) {
            throw new AssistantProviderConfigurationException("Unknown provider: " + providerName);
        }
        validateProvider(providerName, settings);
        return settings;
    }

    private void validateProvider(String providerName, AiAssistantProperties.ProviderSettings settings) {
        if (!settings.isEnabled()) {
            throw new AssistantProviderConfigurationException("Active provider '" + providerName + "' is disabled.");
        }
        if (!StringUtils.hasText(settings.getType())) {
            throw new AssistantProviderConfigurationException("Provider '" + providerName + "' is missing type.");
        }
        if (!StringUtils.hasText(settings.getModel())) {
            throw new AssistantProviderConfigurationException("Provider '" + providerName + "' is missing model.");
        }
        if (!StringUtils.hasText(settings.getBaseUrl())) {
            throw new AssistantProviderConfigurationException("Provider '" + providerName + "' is missing baseUrl.");
        }
        if (settings.requiresApiKey() && !StringUtils.hasText(settings.getApiKey())) {
            throw new AssistantProviderConfigurationException("Provider '" + providerName + "' requires an API key environment variable.");
        }
    }

    private boolean includesMutatingTool(AssistantResponse response) {
        return response.toolsCalled().stream().anyMatch(tool -> tool.mutating());
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public static class AssistantProviderConfigurationException extends RuntimeException {

        public AssistantProviderConfigurationException(String message) {
            super(message);
        }
    }
}
