package com.example.orders;

import java.math.BigDecimal;

public class OrderResult {
    private final Long orderId;
    private final BigDecimal totalAmount;
    private final String status;

    public OrderResult(Long orderId, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }
}
