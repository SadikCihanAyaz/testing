package com.example.orders;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderService orderService,
                           OrderRepository orderRepository,
                           ProductRepository productRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable("orderId") Long orderId) {
        return getExistingOrder(orderId);
    }

    @PostMapping
    public ResponseEntity<OrderResult> placeOrder(@RequestBody CreateOrderApiRequest request) {
        OrderRequest orderRequest = new OrderRequest(request.customerId(), request.productId(), request.quantity());
        OrderResult result = orderService.placeOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{orderId}")
    public Order replaceOrder(@PathVariable("orderId") Long orderId, @RequestBody UpsertOrderApiRequest request) {
        getExistingOrder(orderId);
        String customerId = requireNotBlank(request.customerId(), "Customer id cannot be blank");
        String productId = requireNotBlank(request.productId(), "Product id cannot be blank");
        int quantity = requirePositive(request.quantity(), "Quantity must be greater than zero");
        BigDecimal totalAmount = calculateTotalAmount(productId, quantity);

        Order updated = new Order(orderId, customerId, productId, quantity, totalAmount);
        return orderRepository.save(updated);
    }

    @PatchMapping("/{orderId}")
    public Order patchOrder(@PathVariable("orderId") Long orderId, @RequestBody UpsertOrderApiRequest request) {
        Order existingOrder = getExistingOrder(orderId);

        String customerId = request.customerId() == null
                ? existingOrder.getCustomerId()
                : requireNotBlank(request.customerId(), "Customer id cannot be blank");
        String productId = request.productId() == null
                ? existingOrder.getProductId()
                : requireNotBlank(request.productId(), "Product id cannot be blank");
        int quantity = request.quantity() == null
                ? existingOrder.getQuantity()
                : requirePositive(request.quantity(), "Quantity must be greater than zero");

        BigDecimal totalAmount = hasPricingFieldsChanged(existingOrder, productId, quantity)
                ? calculateTotalAmount(productId, quantity)
                : existingOrder.getTotalAmount();

        Order updated = new Order(orderId, customerId, productId, quantity, totalAmount);
        return orderRepository.save(updated);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") Long orderId) {
        getExistingOrder(orderId);
        orderRepository.deleteById(orderId);
        return ResponseEntity.noContent().build();
    }

    private Order getExistingOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }

    private BigDecimal calculateTotalAmount(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    private String requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private int requirePositive(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private boolean hasPricingFieldsChanged(Order existingOrder, String productId, int quantity) {
        return !existingOrder.getProductId().equals(productId) || existingOrder.getQuantity() != quantity;
    }
}
