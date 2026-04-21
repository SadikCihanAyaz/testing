package com.example.orders;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryOrderRepository implements OrderRepository {

    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        Long orderId;
        if (order.getId() == null) {
            orderId = idSequence.getAndIncrement();
        } else {
            orderId = order.getId();
            long nextId = orderId + 1;
            idSequence.updateAndGet(current -> Math.max(current, nextId));
        }
        Order persistedOrder = new Order(
                orderId,
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalAmount()
        );
        orders.put(orderId, persistedOrder);
        return persistedOrder;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return orders.values()
                .stream()
                .sorted(Comparator.comparing(Order::getId))
                .toList();
    }

    @Override
    public void deleteById(Long orderId) {
        orders.remove(orderId);
    }
}
