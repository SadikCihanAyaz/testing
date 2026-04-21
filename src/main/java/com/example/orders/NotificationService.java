package com.example.orders;

import java.math.BigDecimal;

public interface NotificationService {
    void sendHighValueOrderAlert(Long orderId, String customerId, BigDecimal totalAmount);
}
