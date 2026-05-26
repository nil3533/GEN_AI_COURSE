package com.sani.orderassistant.order;

public record CancelOrderResult(
        String orderId,
        String status,
        String message
) {
}
