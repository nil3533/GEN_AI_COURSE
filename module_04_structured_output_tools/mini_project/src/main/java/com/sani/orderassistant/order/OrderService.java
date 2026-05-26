package com.sani.orderassistant.order;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, OrderRecord> orders = new ConcurrentHashMap<>();

    public OrderService() {
        seed(new OrderRecord("ORD-1001", "cust-100", "Spring AI Handbook", new BigDecimal("1499.00"),
                OrderStatus.SHIPPED, LocalDate.of(2026, 5, 30)));
        seed(new OrderRecord("ORD-1002", "cust-100", "Java 21 Workshop Pass", new BigDecimal("4999.00"),
                OrderStatus.PROCESSING, LocalDate.of(2026, 6, 3)));
        seed(new OrderRecord("ORD-2001", "cust-200", "pgvector Starter Kit", new BigDecimal("2499.00"),
                OrderStatus.DELIVERED, LocalDate.of(2026, 5, 20)));
    }

    public List<OrderSummary> recentOrders(String customerId) {
        return orders.values().stream()
                .filter(order -> order.customerId().equals(customerId))
                .sorted(Comparator.comparing(OrderRecord::orderId).reversed())
                .map(order -> new OrderSummary(order.orderId(), order.item(), order.total(), order.status()))
                .toList();
    }

    public OrderStatusResult getOrderStatus(String customerId, String orderId) {
        var order = orders.get(orderId);
        if (order == null || !order.customerId().equals(customerId)) {
            return new OrderStatusResult(orderId, "NOT_FOUND",
                    "No order was found for this customer and order id.", null, false);
        }

        return new OrderStatusResult(
                order.orderId(),
                order.status().name(),
                statusMessage(order),
                order.estimatedDelivery(),
                isCancelable(order)
        );
    }

    public CancelOrderResult cancelOrder(String customerId, String orderId, String reason) {
        return orders.compute(orderId, (ignored, order) -> {
            if (order == null || !order.customerId().equals(customerId)) {
                return order;
            }
            if (!isCancelable(order)) {
                return order;
            }
            return order.withStatus(OrderStatus.CANCELLED);
        }) == null
                ? new CancelOrderResult(orderId, "NOT_FOUND", "No order was found for this customer and order id.")
                : cancellationResult(customerId, orderId, reason);
    }

    private CancelOrderResult cancellationResult(String customerId, String orderId, String reason) {
        var order = orders.get(orderId);
        if (order == null || !order.customerId().equals(customerId)) {
            return new CancelOrderResult(orderId, "NOT_FOUND", "No order was found for this customer and order id.");
        }
        if (order.status() == OrderStatus.CANCELLED) {
            return new CancelOrderResult(orderId, "CANCELLED", "Order was cancelled. Reason: " + reason);
        }
        return new CancelOrderResult(orderId, "REJECTED",
                "Order cannot be cancelled because it is already " + order.status().name() + ".");
    }

    private boolean isCancelable(OrderRecord order) {
        return order.status() == OrderStatus.PROCESSING;
    }

    private String statusMessage(OrderRecord order) {
        return switch (order.status()) {
            case PROCESSING -> "Order is being prepared and can still be cancelled.";
            case SHIPPED -> "Order has shipped and is on the way.";
            case DELIVERED -> "Order has already been delivered.";
            case CANCELLED -> "Order has been cancelled.";
        };
    }

    private void seed(OrderRecord order) {
        orders.put(order.orderId(), order);
    }
}
