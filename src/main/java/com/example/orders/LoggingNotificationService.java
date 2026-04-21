package com.example.orders;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingNotificationService.class);

    @Override
    public void sendHighValueOrderAlert(Long orderId, String customerId, BigDecimal totalAmount) {
        logger.info("High value order alert. orderId={}, customerId={}, totalAmount={}",
                orderId, customerId, totalAmount);
    }
}
