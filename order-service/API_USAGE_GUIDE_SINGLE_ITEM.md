# Order Service API - Single Item Usage Guide

## Overview
The Order Service has been refactored to support **one-to-one** relationship between Order and OrderItem. Each order now contains exactly **one item** instead of a list of items.

## API Endpoints

### 1. Create Order (POST)
**Endpoint**: `POST /api/orders`

**Request Body** (New Format - Single Item):
```json
{
  "customerId": "CUST001",
  "item": {
    "productId": "PROD001",
    "productName": "Laptop",
    "quantity": 2,
    "unitPrice": 1000.0
  }
}
```

**Response**:
```json
{
  "id": 1,
  "orderNumber": "ORD-1777160937250",
  "status": "PENDING",
  "customerId": "CUST001",
  "totalPrice": 2000.0,
  "item": {
    "id": 1,
    "productId": "PROD001",
    "productName": "Laptop",
    "quantity": 2,
    "unitPrice": 1000.0,
    "totalPrice": 2000.0
  },
  "createdAt": "2026-04-25T19:48:57.261415",
  "updatedAt": "2026-04-25T19:48:57.261447"
}
```

**Notes**:
- `item` is now a single object (NOT an array)
- `totalPrice` is automatically calculated: `quantity × unitPrice`
- `status` is automatically set to "PENDING"
- `orderNumber` is automatically generated

### 2. Get Order by ID (GET)
**Endpoint**: `GET /api/orders/{id}`

**Example**:
```bash
curl -X GET http://localhost:8080/api/orders/1
```

**Response**:
```json
{
  "id": 1,
  "orderNumber": "ORD-1777160937250",
  "status": "PENDING",
  "customerId": "CUST001",
  "totalPrice": 2000.0,
  "item": {
    "id": 1,
    "productId": "PROD001",
    "productName": "Laptop",
    "quantity": 2,
    "unitPrice": 1000.0,
    "totalPrice": 2000.0
  },
  "createdAt": "2026-04-25T19:48:57.261415",
  "updatedAt": "2026-04-25T19:48:57.261447"
}
```

### 3. Get All Orders (GET)
**Endpoint**: `GET /api/orders`
**Optional Parameters**:
- `customerId`: Filter by customer ID
- `status`: Filter by order status

**Examples**:
```bash
# Get all orders
curl -X GET http://localhost:8080/api/orders

# Filter by customer ID
curl -X GET http://localhost:8080/api/orders?customerId=CUST001

# Filter by status
curl -X GET http://localhost:8080/api/orders?status=CONFIRMED
```

**Response**:
```json
[
  {
    "id": 1,
    "orderNumber": "ORD-1777160937250",
    "status": "PENDING",
    "customerId": "CUST001",
    "totalPrice": 2000.0,
    "item": {
      "id": 1,
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 2,
      "unitPrice": 1000.0,
      "totalPrice": 2000.0
    },
    "createdAt": "2026-04-25T19:48:57.261415",
    "updatedAt": "2026-04-25T19:48:57.261447"
  },
  {
    "id": 2,
    "orderNumber": "ORD-1777160981457",
    "status": "PENDING",
    "customerId": "CUST002",
    "totalPrice": 900.0,
    "item": {
      "id": 2,
      "productId": "PROD003",
      "productName": "Monitor",
      "quantity": 3,
      "unitPrice": 300.0,
      "totalPrice": 900.0
    },
    "createdAt": "2026-04-25T19:48:57.261415",
    "updatedAt": "2026-04-25T19:48:57.261447"
  }
]
```

### 4. Update Order (PUT)
**Endpoint**: `PUT /api/orders/{id}`

**Request Body** (Update status and/or item):
```json
{
  "status": "CONFIRMED",
  "item": {
    "productId": "PROD002",
    "productName": "Desktop",
    "quantity": 1,
    "unitPrice": 1500.0
  }
}
```

**Response**:
```json
{
  "id": 1,
  "orderNumber": "ORD-1777160937250",
  "status": "CONFIRMED",
  "customerId": "CUST001",
  "totalPrice": 1500.0,
  "item": {
    "id": 1,
    "productId": "PROD002",
    "productName": "Desktop",
    "quantity": 1,
    "unitPrice": 1500.0,
    "totalPrice": 1500.0
  },
  "createdAt": "2026-04-25T19:48:57.261415",
  "updatedAt": "2026-04-25T19:49:05.530351"
}
```

**Allowed Status Values**:
- `PENDING`
- `CONFIRMED`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Notes**:
- Both `status` and `item` are optional in the request
- `totalPrice` is automatically recalculated if item is updated
- `updatedAt` timestamp is automatically updated

### 5. Delete Order (DELETE)
**Endpoint**: `DELETE /api/orders/{id}`

**Example**:
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

**Response**: 
- HTTP Status: 204 No Content
- Body: Empty

**Notes**:
- Deletes the order and its associated item (cascade delete)
- Returns 404 if order not found

### 6. Health Check (GET)
**Endpoint**: `GET /api/orders/health`

**Example**:
```bash
curl -X GET http://localhost:8080/api/orders/health
```

**Response**:
```json
"Order Service is running"
```

## Data Models

### OrderDTO
```json
{
  "id": 0,
  "orderNumber": "ORD-1234567890",
  "status": "PENDING",
  "customerId": "CUST001",
  "totalPrice": 1050.0,
  "item": {
    "id": 0,
    "productId": "PROD001",
    "productName": "Laptop",
    "quantity": 1,
    "unitPrice": 1000.0,
    "totalPrice": 1000.0
  },
  "createdAt": "2026-04-23T10:30:00",
  "updatedAt": "2026-04-23T10:30:00"
}
```

**Fields**:
- `id` (Long, read-only): Unique order identifier
- `orderNumber` (String, read-only, unique): Auto-generated order number
- `status` (String): Current order status
- `customerId` (String, required): Customer identifier
- `totalPrice` (Double, read-only): Total price (automatically calculated)
- `item` (OrderItemDTO, required): The order item
- `createdAt` (DateTime, read-only): Creation timestamp
- `updatedAt` (DateTime, read-only): Last update timestamp

### OrderItemDTO
```json
{
  "id": 0,
  "productId": "PROD001",
  "productName": "Laptop",
  "quantity": 1,
  "unitPrice": 1000.0,
  "totalPrice": 1000.0
}
```

**Fields**:
- `id` (Long, read-only): Unique item identifier
- `productId` (String, required): Product identifier
- `productName` (String, required): Product name
- `quantity` (Integer, required, minimum 1): Item quantity
- `unitPrice` (Double, required, minimum 0): Price per unit
- `totalPrice` (Double, read-only): Auto-calculated total (quantity × unitPrice)

## cURL Examples

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "item": {
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 1000.0
    }
  }'
```

### Get Order
```bash
curl -X GET http://localhost:8080/api/orders/1
```

### Get All Orders
```bash
curl -X GET http://localhost:8080/api/orders
```

### Update Order
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "item": {
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 2,
      "unitPrice": 1000.0
    }
  }'
```

### Delete Order
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

## HTTP Status Codes

| Code | Description | Endpoints |
|------|-------------|-----------|
| 200 | OK | GET (single/all), PUT |
| 201 | Created | POST |
| 204 | No Content | DELETE |
| 400 | Bad Request | POST, PUT (invalid data) |
| 404 | Not Found | GET (single), PUT, DELETE |
| 500 | Internal Server Error | All endpoints |

## Validation Rules

### Order Creation
- `customerId` is required
- `item` is required and cannot be null

### Item Validation
- `productId` is required (max 255 chars)
- `productName` is required (max 255 chars)
- `quantity` must be ≥ 1
- `unitPrice` must be ≥ 0

## Key Differences from Previous API

| Aspect | Previous | Current |
|--------|----------|---------|
| Items field | `items` (array) | `item` (single object) |
| Create request | Multiple items | Single item |
| Response structure | Contains `items` array | Contains `item` object |
| Database relationship | One-to-Many | One-to-One |
| Item per order | Multiple | Exactly one |

## Swagger UI

Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

View the OpenAPI specification:
```
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml
```


