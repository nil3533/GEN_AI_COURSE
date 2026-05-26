package com.sani.orderassistant.order;

import java.math.BigDecimal;

public record OrderSummary(
        String orderId,
        String item,
        BigDecimal total,
        OrderStatus status
) {
}
