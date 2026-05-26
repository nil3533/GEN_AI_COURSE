package com.sani.orderassistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sani.orderassistant.config.AiAssistantProperties;
import com.sani.orderassistant.dto.AssistantIntent;
import com.sani.orderassistant.dto.AssistantRequest;
import com.sani.orderassistant.dto.AssistantResponse;
import com.sani.orderassistant.order.OrderService;
import com.sani.orderassistant.tools.OrderTools;
import com.sani.orderassistant.tools.ToolCallRecorder;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssistantServiceTest {

    @Test
    void responseIncludesToolTracesFromGatewayExecution() {
        var recorder = new ToolCallRecorder(new ObjectMapper());
        var tools = new OrderTools(new OrderService(), recorder);
        var service = new AssistantService(properties(), new StatusGateway(tools), recorder);

        var response = service.respond(new AssistantRequest("cust-100", "Where is ORD-1001?", false));

        assertThat(response.intent()).isEqualTo(AssistantIntent.ORDER_STATUS);
        assertThat(response.toolsCalled()).hasSize(1);
        assertThat(response.toolsCalled().getFirst().name()).isEqualTo("getOrderStatus");
        assertThat(response.toolsCalled().getFirst().result()).contains("SHIPPED");
    }

    @Test
    void mutatingToolWithoutConfirmationAddsSafetyNote() {
        var recorder = new ToolCallRecorder(new ObjectMapper());
        var tools = new OrderTools(new OrderService(), recorder);
        var service = new AssistantService(properties(), new CancelGateway(tools), recorder);

        var response = service.respond(new AssistantRequest("cust-100", "Cancel ORD-1002", false));

        assertThat(response.toolsCalled()).hasSize(1);
        assertThat(response.toolsCalled().getFirst().mutating()).isTrue();
        assertThat(response.safetyNotes()).contains("Mutating tool calls require explicit confirmation.");
        assertThat(response.toolsCalled().getFirst().result()).contains("REJECTED");
    }

    private AiAssistantProperties properties() {
        var properties = new AiAssistantProperties();
        properties.setActiveProvider("fake");
        properties.setMaxToolCalls(5);
        properties.setProviders(new LinkedHashMap<>());
        properties.getProviders().put("fake", provider());
        return properties;
    }

    private AiAssistantProperties.ProviderSettings provider() {
        var provider = new AiAssistantProperties.ProviderSettings();
        provider.setEnabled(true);
        provider.setType("fake");
        provider.setBaseUrl("https://example.test");
        provider.setModel("fake-model");
        provider.setTemperature(0.1);
        provider.setMaxTokens(100);
        return provider;
    }

    private record StatusGateway(OrderTools tools) implements AssistantGateway {

        @Override
        public AssistantResponse generate(AssistantRequest request, AiAssistantProperties.ProviderSettings settings) {
            var status = tools.getOrderStatus("ORD-1001");
            return new AssistantResponse(
                    AssistantIntent.ORDER_STATUS,
                    status.message(),
                    false,
                    List.of(),
                    List.of(),
                    0.95
            );
        }
    }

    private record CancelGateway(OrderTools tools) implements AssistantGateway {

        @Override
        public AssistantResponse generate(AssistantRequest request, AiAssistantProperties.ProviderSettings settings) {
            var result = tools.cancelOrder("ORD-1002", "changed mind");
            return new AssistantResponse(
                    AssistantIntent.CANCEL_ORDER,
                    result.message(),
                    true,
                    List.of(),
                    List.of(),
                    0.9
            );
        }
    }
}
