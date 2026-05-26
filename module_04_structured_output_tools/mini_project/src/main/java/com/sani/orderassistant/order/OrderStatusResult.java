package com.sani.orderassistant.order;

import java.time.LocalDate;

public record OrderStatusResult(
        String orderId,
        String status,
        String message,
        LocalDate estimatedDelivery,
        boolean cancelable
) {
}
