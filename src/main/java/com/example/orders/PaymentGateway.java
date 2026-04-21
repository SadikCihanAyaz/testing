package com.example.orders;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(String customerId, BigDecimal amount);
}
