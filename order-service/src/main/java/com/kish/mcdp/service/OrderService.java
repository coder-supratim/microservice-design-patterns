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
        Order order = Order.builder()
            .orderNumber(generateOrderNumber())
            .status("PENDING")
            .customerId(orderDTO.getCustomerId())
            .totalPrice(calculateTotalPrice(orderDTO.getItems()))
            .items(convertItemDTOsToEntities(orderDTO.getItems(), null))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

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
        if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
            order.getItems().clear();
            List<OrderItem> newItems = convertItemDTOsToEntities(orderDTO.getItems(), order);
            order.setItems(newItems);
            order.setTotalPrice(calculateTotalPrice(orderDTO.getItems()));
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

    private Double calculateTotalPrice(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
            .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
            .sum();
    }

    private List<OrderItem> convertItemDTOsToEntities(List<OrderItemDTO> itemDTOs, Order order) {
        if (itemDTOs == null) {
            return null;
        }
        return itemDTOs.stream()
            .map(dto -> OrderItem.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .totalPrice(dto.getQuantity() * dto.getUnitPrice())
                .order(order)
                .build())
            .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = null;
        if (order.getItems() != null) {
            itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .totalPrice(item.getTotalPrice())
                    .build())
                .collect(Collectors.toList());
        }

        return OrderDTO.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .customerId(order.getCustomerId())
            .totalPrice(order.getTotalPrice())
            .items(itemDTOs)
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}

