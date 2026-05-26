package com.sani.orderassistant.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sani.orderassistant.order.OrderService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderToolsTest {

    @Test
    void cancelOrderRejectsWhenConfirmationIsMissing() {
        var recorder = new ToolCallRecorder(new ObjectMapper());
        var tools = new OrderTools(new OrderService(), recorder);

        try (var ignored = recorder.open("cust-100", false, 5)) {
            var result = tools.cancelOrder("ORD-1002", "changed mind");

            assertThat(result.status()).isEqualTo("REJECTED");
            assertThat(recorder.snapshot()).hasSize(1);
            assertThat(recorder.snapshot().getFirst().mutating()).isTrue();
        }
    }

    @Test
    void cancelOrderSucceedsWhenConfirmationIsPresent() {
        var recorder = new ToolCallRecorder(new ObjectMapper());
        var tools = new OrderTools(new OrderService(), recorder);

        try (var ignored = recorder.open("cust-100", true, 5)) {
            var result = tools.cancelOrder("ORD-1002", "changed mind");

            assertThat(result.status()).isEqualTo("CANCELLED");
            assertThat(recorder.snapshot()).hasSize(1);
        }
    }

    @Test
    void toolCallBudgetStopsAdditionalCalls() {
        var recorder = new ToolCallRecorder(new ObjectMapper());
        var tools = new OrderTools(new OrderService(), recorder);

        try (var ignored = recorder.open("cust-100", true, 1)) {
            tools.getOrderStatus("ORD-1001");

            assertThatThrownBy(() -> tools.getRecentOrders("cust-100"))
                    .isInstanceOf(ToolCallRecorder.ToolBudgetExceededException.class)
                    .hasMessageContaining("budget exceeded");
        }
    }
}
