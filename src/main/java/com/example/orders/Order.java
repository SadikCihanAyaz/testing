package com.example.orders;

import java.math.BigDecimal;

public class Order {
    private final Long id;
    private final String customerId;
    private final String productId;
    private final int quantity;
    private final BigDecimal totalAmount;

    public Order(Long id, String customerId, String productId, int quantity, BigDecimal totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
