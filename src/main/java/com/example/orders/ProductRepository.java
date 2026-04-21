package com.example.orders;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(String productId);
    Product save(Product product);
}
