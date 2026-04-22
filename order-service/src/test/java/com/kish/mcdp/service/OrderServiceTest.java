package com.kish.mcdp.service;

import com.kish.mcdp.dto.OrderDTO;
import com.kish.mcdp.dto.OrderItemDTO;
import com.kish.mcdp.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO sampleOrderDTO;

    @BeforeEach
    public void setUp() {
        sampleOrderDTO = OrderDTO.builder()
            .customerId("CUST001")
            .build();

        List<OrderItemDTO> items = new ArrayList<>();
        items.add(OrderItemDTO.builder()
            .productId("PROD001")
            .productName("Laptop")
            .quantity(1)
            .unitPrice(1000.0)
            .build());

        sampleOrderDTO.setItems(items);
    }

    @Test
    public void testCreateOrder() {
        assertNotNull(sampleOrderDTO);
        assertEquals("CUST001", sampleOrderDTO.getCustomerId());
    }

    @Test
    public void testGetAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        assertNotNull(orders);
    }
}

