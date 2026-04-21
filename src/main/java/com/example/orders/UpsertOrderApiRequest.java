package com.example.orders;

public record UpsertOrderApiRequest(String customerId, String productId, Integer quantity) {
}
