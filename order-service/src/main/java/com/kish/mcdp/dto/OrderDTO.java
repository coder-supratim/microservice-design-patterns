package com.kish.mcdp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    name = "OrderDTO",
    description = "Data Transfer Object for Order",
    example = "{\"id\":1,\"orderNumber\":\"ORD-1234567890\",\"status\":\"PENDING\",\"customerId\":\"CUST001\",\"totalPrice\":1050.0,\"item\":{\"id\":1,\"productId\":\"PROD001\",\"productName\":\"Laptop\",\"quantity\":1,\"unitPrice\":1000.0,\"totalPrice\":1000.0}}"
)
public class OrderDTO {

    @Schema(description = "Unique order identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Unique order number", example = "ORD-1234567890", accessMode = Schema.AccessMode.READ_ONLY)
    private String orderNumber;

    @Schema(description = "Order status", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "Customer identifier", example = "CUST001", required = true)
    private String customerId;

    @Schema(description = "Total order price", example = "1050.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double totalPrice;

    @Schema(description = "Order item", required = true)
    private OrderItemDTO item;

    @Schema(description = "Order creation timestamp", example = "2026-04-23T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Order last update timestamp", example = "2026-04-23T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}



