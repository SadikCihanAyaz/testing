package com.example.orders;

public record CreateOrderApiRequest(String customerId, String productId, int quantity) {
}
