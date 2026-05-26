package com.sani.orderassistant.order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderRecord(
        String orderId,
        String customerId,
        String item,
        BigDecimal total,
        OrderStatus status,
        LocalDate estimatedDelivery
) {

    public OrderRecord withStatus(OrderStatus nextStatus) {
        return new OrderRecord(orderId, customerId, item, total, nextStatus, estimatedDelivery);
    }
}
