# Quick Start Guide

## Prerequisites
- Java 21
- Maven 3.6+
- PostgreSQL 12+ (running on localhost:5432)

## Step 1: Setup PostgreSQL Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE order_db;

# Exit psql
\q
```

## Step 2: Create Database Tables

The application will auto-create tables due to `ddl-auto: update` configuration, but you can manually create them:

```bash
psql -U postgres -d order_db -f src/main/resources/schema.sql
```

## Step 3: Configure Database Connection (if needed)

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
    username: postgres         # Change if different
    password: postgres         # Change if different
```

## Step 4: Run the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Step 5: Test the API

### Health Check
```bash
curl http://localhost:8080/api/orders/health
# Response: Order Service is running
```

### Create an Order
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

### Get All Orders
```bash
curl http://localhost:8080/api/orders
```

### Get Orders by Customer
```bash
curl "http://localhost:8080/api/orders?customerId=CUST001"
```

### Get Orders by Status
```bash
curl "http://localhost:8080/api/orders?status=PENDING"
```

### Get Specific Order
```bash
curl http://localhost:8080/api/orders/1
```

### Update Order
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED"
  }'
```

### Delete Order
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

## Database Tables

### orders
- `id` - Unique identifier (Primary Key)
- `order_number` - Unique order number (auto-generated)
- `status` - Order status (PENDING, CONFIRMED, SHIPPED, etc.)
- `customer_id` - Customer identifier
- `total_price` - Total order price
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

### order_items
- `id` - Unique identifier (Primary Key)
- `order_id` - Reference to parent order (Foreign Key)
- `product_id` - Product identifier
- `product_name` - Product name
- `quantity` - Item quantity
- `unit_price` - Price per unit
- `total_price` - Total price for this item

## Troubleshooting

### PostgreSQL Connection Error
- Ensure PostgreSQL is running: `pg_isready`
- Check database exists: `psql -U postgres -l | grep order_db`
- Verify credentials in `application.yaml`

### Tables Not Created
- Check logs for Hibernate errors
- Manually run `schema.sql`: `psql -U postgres -d order_db -f src/main/resources/schema.sql`
- Check database permissions

### Port Already in Use
If port 8080 is already in use, change it in `application.yaml`:
```yaml
server:
  port: 8081  # Change to another port
```

## Additional Resources

- [API Documentation](API_DOCUMENTATION.md)
- [Database Setup Guide](DATABASE_SETUP.md)
- [README](README.md)

## Building the JAR

```bash
./mvnw clean package

# Run the JAR
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

## Development

For development with hot reload:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

DevTools will automatically restart the application when files change.

