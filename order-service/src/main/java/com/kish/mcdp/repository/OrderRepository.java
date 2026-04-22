package com.kish.mcdp.repository;

import com.kish.mcdp.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {

    private final ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findAll() {
        return orders.values().stream().toList();
    }

    public void deleteById(Long id) {
        orders.remove(id);
    }

    public void delete(Order order) {
        if (order.getId() != null) {
            orders.remove(order.getId());
        }
    }

    public boolean existsById(Long id) {
        return orders.containsKey(id);
    }

    public long count() {
        return orders.size();
    }
}

