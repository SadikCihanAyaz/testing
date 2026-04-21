package com.example.orders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderAppConfiguration {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("500");

    private static final List<ProductSeed> PRODUCT_SEEDS = List.of(
            new ProductSeed("p-100", "Mechanical Keyboard", new BigDecimal("120"), 50, true),
            new ProductSeed("p-200", "4K Monitor", new BigDecimal("350"), 30, true),
            new ProductSeed("p-300", "Wireless Mouse", new BigDecimal("80"), 120, true),
            new ProductSeed("p-400", "USB-C Dock", new BigDecimal("150"), 40, true),
            new ProductSeed("p-500", "Noise Cancelling Headphones", new BigDecimal("220"), 25, true),
            new ProductSeed("p-600", "Ergonomic Chair", new BigDecimal("480"), 20, true),
            new ProductSeed("p-700", "Laptop Stand", new BigDecimal("60"), 60, true),
            new ProductSeed("p-800", "Webcam Pro", new BigDecimal("140"), 35, true),
            new ProductSeed("p-900", "Legacy Mouse", new BigDecimal("40"), 100, false),
            new ProductSeed("p-1000", "Gaming Laptop", new BigDecimal("1200"), 12, true)
    );

    private static final List<OrderSeed> ORDER_SEEDS = List.of(
            new OrderSeed(1L, "cust-101", "p-100", 2),
            new OrderSeed(2L, "cust-102", "p-200", 1),
            new OrderSeed(3L, "cust-103", "p-400", 2),
            new OrderSeed(4L, "cust-104", "p-500", 1),
            new OrderSeed(5L, "cust-105", "p-600", 1),
            new OrderSeed(6L, "cust-106", "p-1000", 1),
            new OrderSeed(7L, "cust-107", "p-300", 3),
            new OrderSeed(8L, "cust-108", "p-700", 4),
            new OrderSeed(9L, "cust-109", "p-800", 2),
            new OrderSeed(10L, "cust-110", "p-200", 2)
    );

    private static final Map<String, Boolean> PAYMENT_SEEDS = Map.of(
            "cust-101", true,
            "cust-102", true,
            "cust-103", true,
            "cust-104", true,
            "cust-105", true,
            "cust-106", true,
            "cust-107", true,
            "cust-108", true,
            "cust-109", true,
            "cust-110", true
    );

    @Bean
    public ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public PaymentGateway paymentGateway(@Value("${app.payment.fail-customer-prefix:FAIL}") String failPrefix) {
        return new SimplePaymentGateway(failPrefix, PAYMENT_SEEDS);
    }

    @Bean
    public NotificationService notificationService() {
        return new LoggingNotificationService();
    }

    @Bean
    public OrderService orderService(ProductRepository productRepository,
                                     OrderRepository orderRepository,
                                     PaymentGateway paymentGateway,
                                     NotificationService notificationService) {
        return new OrderService(productRepository, orderRepository, paymentGateway, notificationService);
    }

    @Bean
    public CommandLineRunner seedData(ProductRepository productRepository,
                                      OrderRepository orderRepository,
                                      NotificationService notificationService) {
        return args -> {
            Map<String, ProductSeed> productById = PRODUCT_SEEDS.stream()
                    .collect(Collectors.toMap(ProductSeed::id, Function.identity()));
            Map<String, Integer> orderedQuantityByProduct = ORDER_SEEDS.stream()
                    .collect(Collectors.groupingBy(OrderSeed::productId, Collectors.summingInt(OrderSeed::quantity)));

            for (ProductSeed productSeed : PRODUCT_SEEDS) {
                int orderedQty = orderedQuantityByProduct.getOrDefault(productSeed.id(), 0);
                int currentStock = productSeed.initialStock() - orderedQty;
                if (currentStock < 0) {
                    throw new IllegalStateException("Invalid seed stock for product: " + productSeed.id());
                }
                productRepository.save(new Product(
                        productSeed.id(),
                        productSeed.name(),
                        productSeed.price(),
                        currentStock,
                        productSeed.active()
                ));
            }

            for (OrderSeed orderSeed : ORDER_SEEDS) {
                ProductSeed product = productById.get(orderSeed.productId());
                if (product == null) {
                    throw new IllegalStateException("Unknown product in order seed: " + orderSeed.productId());
                }
                BigDecimal totalAmount = product.price().multiply(BigDecimal.valueOf(orderSeed.quantity()));
                Order savedOrder = orderRepository.save(new Order(
                        orderSeed.id(),
                        orderSeed.customerId(),
                        orderSeed.productId(),
                        orderSeed.quantity(),
                        totalAmount
                ));

                if (totalAmount.compareTo(HIGH_VALUE_THRESHOLD) >= 0) {
                    notificationService.sendHighValueOrderAlert(
                            savedOrder.getId(),
                            savedOrder.getCustomerId(),
                            totalAmount
                    );
                }
            }
        };
    }

    private record ProductSeed(String id, String name, BigDecimal price, int initialStock, boolean active) {
    }

    private record OrderSeed(Long id, String customerId, String productId, int quantity) {
    }
}
