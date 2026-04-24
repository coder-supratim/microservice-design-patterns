# Complete Testing Guide

## Prerequisites
- PostgreSQL running on localhost:5432
- Order Service running on localhost:8080
- `curl` or similar HTTP client

## Setup Before Testing

### 1. Create Database
```bash
psql -U postgres
CREATE DATABASE order_db;
\q
```

### 2. Start the Application
```bash
cd /Users/kishorevanam/git/microservice-design-patterns/order-service
./mvnw spring-boot:run
```

Application will start on `http://localhost:8080`

---

## Test Suite

### 1. Health Check ✅
```bash
curl -X GET http://localhost:8080/api/orders/health
```
**Expected Response:** `Order Service is running`

---

### 2. Create Order - Basic ✅
```bash
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
```
**Expected:** 201 Created with order details including generated ID and order number

---

### 3. Create Order - Multiple Items ✅
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST002",
    "items": [
      {
        "productId": "PROD002",
        "productName": "Mouse",
        "quantity": 2,
        "unitPrice": 25.0
      },
      {
        "productId": "PROD003",
        "productName": "Keyboard",
        "quantity": 1,
        "unitPrice": 75.0
      }
    ]
  }'
```
**Expected:** 201 Created, total price = 125.0

---

### 4. Get All Orders ✅
```bash
curl -X GET http://localhost:8080/api/orders
```
**Expected:** 200 OK with JSON array of all orders

---

### 5. Get Orders by Customer ID ✅
```bash
curl -X GET "http://localhost:8080/api/orders?customerId=CUST001"
```
**Expected:** 200 OK with only CUST001's orders

```bash
curl -X GET "http://localhost:8080/api/orders?customerId=CUST002"
```
**Expected:** 200 OK with only CUST002's orders

---

### 6. Get Orders by Status ✅
```bash
# Get PENDING orders
curl -X GET "http://localhost:8080/api/orders?status=PENDING"
```
**Expected:** 200 OK with PENDING orders

```bash
# Get CONFIRMED orders (should be empty initially)
curl -X GET "http://localhost:8080/api/orders?status=CONFIRMED"
```
**Expected:** 200 OK with empty array initially

---

### 7. Get Single Order by ID ✅
```bash
# First, create an order to get an ID
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST003",
    "items": [{"productId": "PROD004", "productName": "Monitor", "quantity": 1, "unitPrice": 300.0}]
  }' | grep '"id"'

# Get that order (replace 1 with actual ID)
curl -X GET http://localhost:8080/api/orders/1
```
**Expected:** 200 OK with full order details

---

### 8. Get Non-existent Order ✅
```bash
curl -X GET http://localhost:8080/api/orders/99999
```
**Expected:** 404 Not Found with error message

---

### 9. Update Order - Status Only ✅
```bash
# Get an order ID first, then update status
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED"
  }'
```
**Expected:** 200 OK with updated status

---

### 10. Update Order - With New Items ✅
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHIPPED",
    "items": [
      {
        "productId": "PROD005",
        "productName": "USB Cable",
        "quantity": 3,
        "unitPrice": 10.0
      }
    ]
  }'
```
**Expected:** 200 OK with new items and recalculated total (30.0)

---

### 11. Delete Order ✅
```bash
# Create an order to delete
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST_DELETE",
    "items": [{"productId": "PROD_TEMP", "productName": "Temp Item", "quantity": 1, "unitPrice": 50.0}]
  }' | grep '"id"'

# Delete it (replace 2 with actual ID)
curl -X DELETE http://localhost:8080/api/orders/2
```
**Expected:** 204 No Content (success, no response body)

---

### 12. Delete Non-existent Order ✅
```bash
curl -X DELETE http://localhost:8080/api/orders/99999
```
**Expected:** 404 Not Found with error message

---

### 13. Verify Data Persistence (Database Check) ✅
```bash
# Connect to database
psql -U postgres -d order_db

# Check orders table
SELECT * FROM orders;

# Check order_items table
SELECT * FROM order_items;

# Check relationship
SELECT o.id, o.order_number, o.status, oi.product_name, oi.quantity 
FROM orders o 
JOIN order_items oi ON o.id = oi.order_id;

# Exit
\q
```

---

## Comprehensive Test Sequence

Run this sequence to fully test all functionality:

```bash
#!/bin/bash

echo "1. Health Check"
curl -X GET http://localhost:8080/api/orders/health
echo -e "\n\n"

echo "2. Create Order 1"
RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "items": [
      {"productId": "PROD001", "productName": "Laptop", "quantity": 1, "unitPrice": 1000.0},
      {"productId": "PROD002", "productName": "Mouse", "quantity": 2, "unitPrice": 25.0}
    ]
  }')
echo "$RESPONSE" | jq .
ORDER1_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "Created Order ID: $ORDER1_ID"
echo -e "\n\n"

echo "3. Create Order 2"
RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST002",
    "items": [
      {"productId": "PROD003", "productName": "Keyboard", "quantity": 1, "unitPrice": 75.0}
    ]
  }')
echo "$RESPONSE" | jq .
ORDER2_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "Created Order ID: $ORDER2_ID"
echo -e "\n\n"

echo "4. Get All Orders"
curl -s -X GET http://localhost:8080/api/orders | jq .
echo -e "\n\n"

echo "5. Get Orders by Customer (CUST001)"
curl -s -X GET "http://localhost:8080/api/orders?customerId=CUST001" | jq .
echo -e "\n\n"

echo "6. Get Orders by Status (PENDING)"
curl -s -X GET "http://localhost:8080/api/orders?status=PENDING" | jq .
echo -e "\n\n"

echo "7. Get Single Order ($ORDER1_ID)"
curl -s -X GET http://localhost:8080/api/orders/$ORDER1_ID | jq .
echo -e "\n\n"

echo "8. Update Order Status ($ORDER1_ID to CONFIRMED)"
curl -s -X PUT http://localhost:8080/api/orders/$ORDER1_ID \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}' | jq .
echo -e "\n\n"

echo "9. Get Updated Order ($ORDER1_ID)"
curl -s -X GET http://localhost:8080/api/orders/$ORDER1_ID | jq .
echo -e "\n\n"

echo "10. Delete Order ($ORDER2_ID)"
curl -s -X DELETE http://localhost:8080/api/orders/$ORDER2_ID -w "\nStatus: %{http_code}\n"
echo -e "\n\n"

echo "11. Verify Deletion (Get All Orders)"
curl -s -X GET http://localhost:8080/api/orders | jq .
echo -e "\n\n"

echo "12. Try to Get Deleted Order (should fail)"
curl -s -X GET http://localhost:8080/api/orders/$ORDER2_ID | jq .
echo -e "\n"
```

Save as `test_api.sh`, make executable with `chmod +x test_api.sh`, and run with `./test_api.sh`

---

## Database Verification Commands

After running tests, verify data in PostgreSQL:

```bash
# Connect to database
psql -U postgres -d order_db

# View all orders
SELECT id, order_number, status, customer_id, total_price, created_at 
FROM orders;

# View all order items
SELECT oi.id, oi.order_id, oi.product_name, oi.quantity, oi.unit_price, oi.total_price
FROM order_items oi
ORDER BY oi.order_id;

# View with relationships
SELECT 
  o.id,
  o.order_number,
  o.status,
  o.customer_id,
  o.total_price,
  COUNT(oi.id) as item_count,
  o.created_at
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id
ORDER BY o.created_at DESC;

# Count totals
SELECT 
  (SELECT COUNT(*) FROM orders) as total_orders,
  (SELECT COUNT(*) FROM order_items) as total_items;

# Exit psql
\q
```

---

## Common Issues & Debugging

### Connection Issues
```bash
# Check if PostgreSQL is running
pg_isready

# Check if application is running
curl http://localhost:8080/api/orders/health
```

### View Application Logs
```bash
# When running with Maven
./mvnw spring-boot:run | grep -i error

# Check for SQL errors
./mvnw spring-boot:run | grep -i "sql\|hibernate"
```

### Reset Database
```bash
psql -U postgres -d order_db

DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

\q

# Tables will be auto-created on next request
```

---

## Success Criteria

✅ All endpoints return correct HTTP status codes
✅ Data persists in PostgreSQL between requests
✅ Relationships work (orders have items)
✅ Filtering by customerId works
✅ Filtering by status works
✅ Cascade delete removes order items
✅ Timestamps are automatically set
✅ Total price is correctly calculated
✅ Error handling returns proper error messages
✅ No SQL errors in logs

