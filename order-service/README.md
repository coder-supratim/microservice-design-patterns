# Order Service

A Spring Boot microservice for managing orders in a microservice architecture.

## Prerequisites

- Java 21
- Maven 3.6+

## Dependencies

- Spring Boot Web MVC
- Spring Boot DevTools (development)
- Lombok (for reducing boilerplate code)

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

## Running the Application

1. Clone the repository.
2. Navigate to the project directory.
3. Run the application using Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

   Or using Maven directly:

   ```bash
   mvn spring-boot:run
   ```

The application will start on the default port 8080.

## REST API Endpoints

### Health Check
- **GET** `/api/orders/health` - Check if the service is running

### Order Management
- **POST** `/api/orders` - Create a new order
- **GET** `/api/orders` - Get all orders
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

The application configuration is in `src/main/resources/application.yaml`. Currently, it sets the application name to `order-service`.

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
