package com.sani.orderassistant.tools;

import com.sani.orderassistant.order.CancelOrderResult;
import com.sani.orderassistant.order.OrderService;
import com.sani.orderassistant.order.OrderStatusResult;
import com.sani.orderassistant.order.OrderSummary;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderTools {

    private final OrderService orderService;

    private final ToolCallRecorder recorder;

    public OrderTools(OrderService orderService, ToolCallRecorder recorder) {
        this.orderService = orderService;
        this.recorder = recorder;
    }

    @Tool(
            name = "getOrderStatus",
            description = "Get fulfillment and delivery status for exactly one order id owned by the current customer."
    )
    public OrderStatusResult getOrderStatus(
            @ToolParam(required = true, description = "Order id such as ORD-1001") String orderId) {
        return recorder.record("getOrderStatus", Map.of("orderId", orderId), false,
                () -> orderService.getOrderStatus(recorder.customerId(), orderId));
    }

    @Tool(
            name = "getRecentOrders",
            description = "List recent orders for the current customer. Use when the user asks what they ordered recently."
    )
    public List<OrderSummary> getRecentOrders(
            @ToolParam(required = true, description = "Customer id such as cust-100") String customerId) {
        return recorder.record("getRecentOrders", Map.of("customerId", customerId), false, () -> {
            if (!customerId.equals(recorder.customerId())) {
                return List.of();
            }
            return orderService.recentOrders(customerId);
        });
    }

    @Tool(
            name = "cancelOrder",
            description = "Cancel exactly one processing order. Do not call unless the user explicitly confirmed cancellation."
    )
    public CancelOrderResult cancelOrder(
            @ToolParam(required = true, description = "Order id such as ORD-1002") String orderId,
            @ToolParam(required = true, description = "Short human reason for cancellation") String reason) {
        return recorder.record("cancelOrder", Map.of("orderId", orderId, "reason", reason), true, () -> {
            if (!recorder.cancellationConfirmed()) {
                return new CancelOrderResult(orderId, "REJECTED",
                        "Cancellation requires explicit confirmation. Ask the user to confirm first.");
            }
            return orderService.cancelOrder(recorder.customerId(), orderId, reason);
        });
    }
}
