# Order Service API Documentation

## Base URL
```
http://localhost:8080/api/orders
```

## Endpoints

### 1. Health Check
**Endpoint:** `GET /api/orders/health`

**Description:** Check if the Order Service is running.

**Response:**
```
Status: 200 OK
Body: "Order Service is running"
```

---

### 2. Create Order
**Endpoint:** `POST /api/orders`

**Description:** Create a new order.

**Request Body:**
```json
{
  "customerId": "CUST001",
  "items": [
    {
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 1000.0
    },
    {
      "productId": "PROD002",
      "productName": "Mouse",
      "quantity": 2,
      "unitPrice": 25.0
    }
  ]
}
```

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-1713798600000",
  "status": "PENDING",
  "customerId": "CUST001",
  "totalPrice": 1050.0,
  "items": [
    {
      "id": null,
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 1000.0,
      "totalPrice": 1000.0
    },
    {
      "id": null,
      "productId": "PROD002",
      "productName": "Mouse",
      "quantity": 2,
      "unitPrice": 25.0,
      "totalPrice": 50.0
    }
  ],
  "createdAt": "2026-04-21T21:36:40.123456",
  "updatedAt": "2026-04-21T21:36:40.123456"
}
```

**Status Code:** `201 Created`

---

### 3. Get All Orders
**Endpoint:** `GET /api/orders`

**Description:** Retrieve all orders.

**Response:**
```json
[
  {
    "id": 1,
    "orderNumber": "ORD-1713798600000",
    "status": "PENDING",
    "customerId": "CUST001",
    "totalPrice": 1050.0,
    "items": [...],
    "createdAt": "2026-04-21T21:36:40.123456",
    "updatedAt": "2026-04-21T21:36:40.123456"
  }
]
```

**Status Code:** `200 OK`

---

### 4. Get Order by ID
**Endpoint:** `GET /api/orders/{id}`

**Description:** Retrieve a specific order by ID.

**Path Parameters:**
- `id` (required): The order ID

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-1713798600000",
  "status": "PENDING",
  "customerId": "CUST001",
  "totalPrice": 1050.0,
  "items": [...],
  "createdAt": "2026-04-21T21:36:40.123456",
  "updatedAt": "2026-04-21T21:36:40.123456"
}
```

**Status Code:** `200 OK`

**Error Response (Order not found):**
```json
{
  "timestamp": "2026-04-21T21:36:40.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 999"
}
```

**Error Status Code:** `404 Not Found`

---

### 5. Update Order
**Endpoint:** `PUT /api/orders/{id}`

**Description:** Update an existing order.

**Path Parameters:**
- `id` (required): The order ID

**Request Body:**
```json
{
  "status": "CONFIRMED",
  "items": [
    {
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 2,
      "unitPrice": 1000.0
    }
  ]
}
```

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-1713798600000",
  "status": "CONFIRMED",
  "customerId": "CUST001",
  "totalPrice": 2000.0,
  "items": [...],
  "createdAt": "2026-04-21T21:36:40.123456",
  "updatedAt": "2026-04-21T21:36:41.654321"
}
```

**Status Code:** `200 OK`

---

### 6. Delete Order
**Endpoint:** `DELETE /api/orders/{id}`

**Description:** Delete an order.

**Path Parameters:**
- `id` (required): The order ID

**Response:** No content

**Status Code:** `204 No Content`

**Error Response (Order not found):**
```json
{
  "timestamp": "2026-04-21T21:36:40.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with id: 999"
}
```

**Error Status Code:** `404 Not Found`

---

## Error Handling

The API uses standard HTTP status codes and includes a consistent error response format:

```json
{
  "timestamp": "2026-04-21T21:36:40.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "Error details here"
}
```

### Common Error Codes
- `400 Bad Request` - Invalid request parameters or body
- `404 Not Found` - Order not found
- `500 Internal Server Error` - Server-side error

## CORS

The API supports Cross-Origin Resource Sharing (CORS) from all origins with a max age of 3600 seconds.

## Testing with cURL

```bash
# Health check
curl -X GET http://localhost:8080/api/orders/health

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "items": [
      {
        "productId": "PROD001",
        "productName": "Laptop",
        "quantity": 1,
        "unitPrice": 1000.0
      }
    ]
  }'

# Get all orders
curl -X GET http://localhost:8080/api/orders

# Get specific order
curl -X GET http://localhost:8080/api/orders/1

# Update order
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED"
  }'

# Delete order
curl -X DELETE http://localhost:8080/api/orders/1
```

