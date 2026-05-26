package com.sani.orderassistant.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    @Test
    void recentOrdersReturnsOnlyCustomerOrders() {
        var service = new OrderService();

        var orders = service.recentOrders("cust-100");

        assertThat(orders).extracting(OrderSummary::orderId)
                .containsExactly("ORD-1002", "ORD-1001");
    }

    @Test
    void getOrderStatusRejectsCrossCustomerAccess() {
        var service = new OrderService();

        var result = service.getOrderStatus("cust-100", "ORD-2001");

        assertThat(result.status()).isEqualTo("NOT_FOUND");
        assertThat(result.cancelable()).isFalse();
    }

    @Test
    void cancelProcessingOrderChangesStatus() {
        var service = new OrderService();

        var result = service.cancelOrder("cust-100", "ORD-1002", "changed mind");

        assertThat(result.status()).isEqualTo("CANCELLED");
        assertThat(service.getOrderStatus("cust-100", "ORD-1002").status()).isEqualTo("CANCELLED");
    }

    @Test
    void shippedOrderCannotBeCancelled() {
        var service = new OrderService();

        var result = service.cancelOrder("cust-100", "ORD-1001", "too late");

        assertThat(result.status()).isEqualTo("REJECTED");
        assertThat(service.getOrderStatus("cust-100", "ORD-1001").status()).isEqualTo("SHIPPED");
    }
}
