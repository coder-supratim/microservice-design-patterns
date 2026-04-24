package com.kish.mcdp.controller;

import com.kish.mcdp.dto.OrderDTO;
import com.kish.mcdp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Order Management", description = "APIs for managing orders - Create, Read, Update, Delete operations")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
        summary = "Create a new order",
        description = "Creates a new order with the provided customer ID and order items. Automatically generates order number and sets status to PENDING."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order data provided")
    })
    public ResponseEntity<OrderDTO> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Order details to create",
                required = true,
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))
            )
            @RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Order ID to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(
        summary = "Get all orders with optional filtering",
        description = "Retrieves all orders or filters by customer ID or status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class)))
    })
    public ResponseEntity<List<OrderDTO>> getAllOrders(
            @Parameter(description = "Filter orders by customer ID", example = "CUST001", required = false)
            @RequestParam(required = false) String customerId,
            @Parameter(description = "Filter orders by status", example = "PENDING", required = false)
            @RequestParam(required = false) String status) {
        List<OrderDTO> orders;

        if (customerId != null) {
            orders = orderService.getOrdersByCustomerId(customerId);
        } else if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }

        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing order",
        description = "Updates order status and/or items. Recalculates total price if items are updated."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<OrderDTO> updateOrder(
            @Parameter(description = "Order ID to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated order details",
                required = true,
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))
            )
            @RequestBody OrderDTO orderDTO) {
        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete an order",
        description = "Deletes an order and all associated order items"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID to delete", required = true, example = "1")
            @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Checks if the Order Service is running and responsive"
    )
    @ApiResponse(responseCode = "200", description = "Service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is running");
    }
}

