package com.example.orders;

import java.math.BigDecimal;

public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    public OrderService(ProductRepository productRepository,
                        OrderRepository orderRepository,
                        PaymentGateway paymentGateway,
                        NotificationService notificationService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
    }

    public OrderResult placeOrder(OrderRequest request) {
        validateRequest(request);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + request.getProductId()));

        if (!product.isActive()) {
            throw new InactiveProductException("Product is inactive: " + product.getId());
        }

        if (request.getQuantity() > product.getAvailableStock()) {
            throw new InsufficientStockException("Not enough stock for product: " + product.getId());
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        PaymentResult paymentResult = paymentGateway.charge(request.getCustomerId(), totalAmount);

        if (!paymentResult.isSuccessful()) {
            throw new PaymentFailedException("Payment failed for customer: " + request.getCustomerId());
        }

        product.decreaseStock(request.getQuantity());
        productRepository.save(product);

        Order order = new Order(null, request.getCustomerId(), product.getId(), request.getQuantity(), totalAmount);
        Order savedOrder = orderRepository.save(order);

        if (totalAmount.compareTo(new BigDecimal("500")) >= 0) {
            notificationService.sendHighValueOrderAlert(savedOrder.getId(), request.getCustomerId(), totalAmount);
        }

        return new OrderResult(savedOrder.getId(), totalAmount, "CREATED");
    }

    private void validateRequest(OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (isBlank(request.getCustomerId())) {
            throw new IllegalArgumentException("Customer id cannot be blank");
        }
        if (isBlank(request.getProductId())) {
            throw new IllegalArgumentException("Product id cannot be blank");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
