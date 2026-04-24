# Order Service

A Spring Boot microservice for managing orders in a microservice architecture using PostgreSQL for data persistence.

## Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL 12+ running on localhost:5432

## Dependencies

- Spring Boot Web MVC
- Spring Boot Data JPA
- Spring Boot DevTools (development)
- Lombok (for reducing boilerplate code)
- PostgreSQL JDBC Driver

## Project Structure

```
src/main/java/com/kish/mcdp/
├── entity/              # Domain entities (Order, OrderItem)
├── dto/                 # Data Transfer Objects (OrderDTO, OrderItemDTO)
├── repository/          # Data access layer (OrderRepository)
├── service/             # Business logic layer (OrderService)
├── controller/          # REST API endpoints (OrderController)
├── exception/           # Exception handlers (GlobalExceptionHandler)
└── OrderServiceApplication.java  # Main application class
```

## Database Setup

Before running the application, you need to set up PostgreSQL:

1. **Create the database:**
   ```bash
   psql -U postgres
   CREATE DATABASE order_db;
   \q
   ```

2. **Create tables (Hibernate will auto-create them, but you can also run the script):**
   ```bash
   psql -U postgres -d order_db -f src/main/resources/schema.sql
   ```

3. **Update database credentials if needed:**
   Edit `src/main/resources/application.yaml` with your PostgreSQL credentials.

For detailed database setup instructions, see [DATABASE_SETUP.md](DATABASE_SETUP.md)

## Running the Application

1. Ensure PostgreSQL is running and the `order_db` database exists
2. Navigate to the project directory
3. Run the application using Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

   Or using Maven directly:

   ```bash
   mvn spring-boot:run
   ```

The application will start on port 8080 and automatically create tables if they don't exist.

## REST API Endpoints

### Health Check
- **GET** `/api/orders/health` - Check if the service is running

### Order Management
- **POST** `/api/orders` - Create a new order
- **GET** `/api/orders` - Get all orders (supports filtering)
- **GET** `/api/orders?customerId=CUST001` - Get orders by customer ID
- **GET** `/api/orders?status=PENDING` - Get orders by status
- **GET** `/api/orders/{id}` - Get a specific order by ID
- **PUT** `/api/orders/{id}` - Update an existing order
- **DELETE** `/api/orders/{id}` - Delete an order

### Example Request Body (Create/Update Order)

```json
{
  "customerId": "CUST001",
  "status": "PENDING",
  "items": [
    {
      "productId": "PROD001",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 1000.0
    }
  ]
}
```

## Configuration

The application configuration is in `src/main/resources/application.yaml`. Default settings:

```yaml
spring:
  application:
    name: order-service
  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update  # Auto-create/update tables
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

server:
  port: 8080
```

Update these values if your PostgreSQL installation uses different credentials or is on a different host/port.

## Building

To build the project:

```bash
./mvnw clean package
```

This will create a JAR file in the `target` directory.

## Testing

Run tests with:

```bash
./mvnw test
```

## Contributing

Please follow standard coding practices and add tests for new features.

## License

This project is licensed under [License Name] - see the LICENSE file for details.
