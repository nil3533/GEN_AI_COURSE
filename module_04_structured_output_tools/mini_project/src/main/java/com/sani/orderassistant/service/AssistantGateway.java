package com.sani.orderassistant.service;

import com.sani.orderassistant.config.AiAssistantProperties;
import com.sani.orderassistant.dto.AssistantRequest;
import com.sani.orderassistant.dto.AssistantResponse;

public interface AssistantGateway {

    AssistantResponse generate(AssistantRequest request, AiAssistantProperties.ProviderSettings settings);
}
