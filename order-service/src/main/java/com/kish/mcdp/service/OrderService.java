package com.kish.mcdp.service;

import com.kish.mcdp.dto.OrderDTO;
import com.kish.mcdp.dto.OrderItemDTO;
import com.kish.mcdp.entity.Order;
import com.kish.mcdp.entity.OrderItem;
import com.kish.mcdp.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        if (orderDTO.getItem() == null) {
            throw new IllegalArgumentException("Order must contain an item");
        }

        Double totalPrice = orderDTO.getItem().getQuantity() * orderDTO.getItem().getUnitPrice();

        OrderItem item = OrderItem.builder()
            .productId(orderDTO.getItem().getProductId())
            .productName(orderDTO.getItem().getProductName())
            .quantity(orderDTO.getItem().getQuantity())
            .unitPrice(orderDTO.getItem().getUnitPrice())
            .totalPrice(totalPrice)
            .build();

        Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .status("PENDING")
            .customerId(orderDTO.getCustomerId())
            .totalPrice(totalPrice)
            .item(item)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        item.setOrder(order);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (orderDTO.getStatus() != null) {
            order.setStatus(orderDTO.getStatus());
        }

        if (orderDTO.getItem() != null) {
            OrderItem existingItem = order.getItem();
            if (existingItem != null) {
                existingItem.setProductId(orderDTO.getItem().getProductId());
                existingItem.setProductName(orderDTO.getItem().getProductName());
                existingItem.setQuantity(orderDTO.getItem().getQuantity());
                existingItem.setUnitPrice(orderDTO.getItem().getUnitPrice());
                existingItem.setTotalPrice(orderDTO.getItem().getQuantity() * orderDTO.getItem().getUnitPrice());
            } else {
                OrderItem newItem = OrderItem.builder()
                    .productId(orderDTO.getItem().getProductId())
                    .productName(orderDTO.getItem().getProductName())
                    .quantity(orderDTO.getItem().getQuantity())
                    .unitPrice(orderDTO.getItem().getUnitPrice())
                    .totalPrice(orderDTO.getItem().getQuantity() * orderDTO.getItem().getUnitPrice())
                    .order(order)
                    .build();
                order.setItem(newItem);
            }
            order.setTotalPrice(orderDTO.getItem().getQuantity() * orderDTO.getItem().getUnitPrice());
        }

        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderItemDTO itemDTO = null;
        if (order.getItem() != null) {
            itemDTO = OrderItemDTO.builder()
                .id(order.getItem().getId())
                .productId(order.getItem().getProductId())
                .productName(order.getItem().getProductName())
                .quantity(order.getItem().getQuantity())
                .unitPrice(order.getItem().getUnitPrice())
                .totalPrice(order.getItem().getTotalPrice())
                .build();
        }

        return OrderDTO.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .customerId(order.getCustomerId())
            .totalPrice(order.getTotalPrice())
            .item(itemDTO)
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}

