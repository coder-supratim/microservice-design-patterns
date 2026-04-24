package com.kish.mcdp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    name = "OrderItemDTO",
    description = "Data Transfer Object for Order Item",
    example = "{\"id\":1,\"productId\":\"PROD001\",\"productName\":\"Laptop\",\"quantity\":1,\"unitPrice\":1000.0,\"totalPrice\":1000.0}"
)
public class OrderItemDTO {

    @Schema(description = "Unique item identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Product identifier", example = "PROD001", required = true)
    private String productId;

    @Schema(description = "Product name", example = "Laptop", required = true)
    private String productName;

    @Schema(description = "Item quantity", example = "1", minimum = "1", required = true)
    private Integer quantity;

    @Schema(description = "Price per unit", example = "1000.0", minimum = "0", required = true)
    private Double unitPrice;

    @Schema(description = "Total price for this item (quantity × unitPrice)", example = "1000.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double totalPrice;
}

