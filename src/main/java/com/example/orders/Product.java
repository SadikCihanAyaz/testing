package com.example.orders;

import java.math.BigDecimal;

public class Product {
    private final String id;
    private final String name;
    private final BigDecimal price;
    private int availableStock;
    private final boolean active;

    public Product(String id, String name, BigDecimal price, int availableStock, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableStock = availableStock;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public boolean isActive() {
        return active;
    }

    public void decreaseStock(int quantity) {
        this.availableStock -= quantity;
    }
}
