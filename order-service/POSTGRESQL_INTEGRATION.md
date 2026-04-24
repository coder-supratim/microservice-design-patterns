# PostgreSQL Integration Summary

## Overview
The Order Service has been successfully integrated with PostgreSQL for persistent data storage. All CRUD operations are now performed through a relational database with proper transaction management.

## Architecture Changes

### 1. **Dependencies Added**
- `spring-boot-starter-data-jpa` - ORM framework (Hibernate)
- `spring-boot-starter-jdbc` - JDBC support
- `postgresql` (42.7.1) - PostgreSQL JDBC driver

### 2. **JPA Entities**

#### Order Entity (`entity/Order.java`)
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @Column(name = "total_price")
    private Double totalPrice;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Key Features:**
- IDENTITY primary key generation (database auto-increment)
- OneToMany relationship with OrderItem
- CascadeType.ALL ensures items are created/deleted with parent order
- Automatic timestamp management with @CreationTimestamp and @UpdateTimestamp

#### OrderItem Entity (`entity/OrderItem.java`)
```java
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
    
    @Column(name = "total_price")
    private Double totalPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
```

**Key Features:**
- ManyToOne relationship back to Order
- FetchType.LAZY prevents N+1 query problems
- Foreign key constraint with ON DELETE CASCADE

### 3. **Spring Data JPA Repositories**

#### OrderRepository (`repository/OrderRepository.java`)
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(String status);
}
```

**Custom Query Methods:**
- `findByOrderNumber()` - Find order by unique order number
- `findByCustomerId()` - Find all orders for a customer
- `findByStatus()` - Find all orders with a specific status

#### OrderItemRepository (`repository/OrderItemRepository.java`)
```java
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
```

**Benefits:**
- No need to write SQL
- Automatic pagination support
- Type-safe queries
- Spring Data auto-creates implementations

### 4. **Enhanced OrderService**

Updates include:
- `@Transactional` annotation for transaction management
- Support for filtering by `customerId` and `status`
- Proper entity-to-DTO conversion
- Cascade operations for items

**New Methods:**
- `getOrdersByCustomerId(String customerId)` - Filter by customer
- `getOrdersByStatus(String status)` - Filter by status

### 5. **Updated OrderController**

Enhanced endpoint to support filtering:
```java
@GetMapping
public ResponseEntity<List<OrderDTO>> getAllOrders(
        @RequestParam(required = false) String customerId,
        @RequestParam(required = false) String status) {
    // Returns filtered results based on parameters
}
```

### 6. **Database Configuration**

`application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update  # Auto-create/update tables
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false
```

**Configuration Options:**
- `ddl-auto: update` - Auto-creates tables and columns
- Alternative: `ddl-auto: create-drop` for testing
- `format_sql: true` - Pretty-print SQL in logs
- `show-sql: false` - Don't print SQL (set to true for debugging)

## Database Schema

### orders table
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    total_price DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_customer_id ON orders(customer_id);
CREATE INDEX idx_status ON orders(status);
```

### order_items table
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2),
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
```

## CRUD Operations

### Create
```java
POST /api/orders
{
  "customerId": "CUST001",
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
- Automatically generates order number
- Sets status to PENDING
- Saves to database with timestamp

### Read
```java
// All orders
GET /api/orders

// Filter by customer
GET /api/orders?customerId=CUST001

// Filter by status
GET /api/orders?status=PENDING

// Single order
GET /api/orders/{id}
```

### Update
```java
PUT /api/orders/{id}
{
  "status": "CONFIRMED",
  "items": [...]  // Optional
}
```
- Updates only provided fields
- Recalculates total price if items changed
- Updates timestamp automatically

### Delete
```java
DELETE /api/orders/{id}
```
- Deletes order and all related items (cascade)

## Transaction Management

The `@Transactional` annotation ensures:
- **ACID Properties:**
  - Atomicity: All or nothing
  - Consistency: Valid state maintained
  - Isolation: Transaction isolation level
  - Durability: Persisted to database

- **Automatic Rollback:** On exceptions
- **Lazy Loading:** Related entities loaded on demand

## Migration from In-Memory Storage

**Before:** ConcurrentHashMap-based in-memory storage
**After:** PostgreSQL persistence layer

**Changes:**
1. Replaced repository implementation with Spring Data JPA
2. Entities now use JPA annotations
3. All queries go through database
4. Relationships handled by Hibernate
5. Transactions managed by Spring

## Performance Optimizations

1. **Eager Loading:** OrderItems loaded with Order (OneToMany)
2. **Lazy Loading:** In relationships where beneficial
3. **Indexes:** Created on frequently queried columns
4. **Connection Pooling:** HikariCP (default in Spring Boot)

## Security Considerations

1. **SQL Injection:** Protected by parameterized queries (JPA)
2. **Password Storage:** Use environment variables in production
3. **Connection SSL:** Can be enabled in production

## Testing Database Connection

```bash
# Test health
curl http://localhost:8080/api/orders/health

# Create test order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "TEST001",
    "items": [{
      "productId": "P1",
      "productName": "Test Product",
      "quantity": 1,
      "unitPrice": 100.0
    }]
  }'

# Verify in database
psql -U postgres -d order_db -c "SELECT * FROM orders;"
```

## Files Created/Modified

**New Files:**
- `src/main/resources/schema.sql` - Database schema
- `DATABASE_SETUP.md` - Setup instructions
- `QUICK_START.md` - Quick start guide
- `repository/OrderItemRepository.java` - OrderItem repository

**Modified Files:**
- `entity/Order.java` - Added JPA annotations
- `entity/OrderItem.java` - Added JPA annotations
- `repository/OrderRepository.java` - Changed to Spring Data JPA
- `service/OrderService.java` - Updated for JPA and transactions
- `controller/OrderController.java` - Enhanced with filtering
- `pom.xml` - Added JPA and PostgreSQL dependencies
- `application.yaml` - Added database configuration
- `README.md` - Updated with database info

## Next Steps

1. **Production Configuration:**
   - Use environment variables for credentials
   - Enable SSL for database connection
   - Set `ddl-auto: validate` in production

2. **Monitoring:**
   - Add Spring Boot Actuator for metrics
   - Implement logging for operations
   - Monitor database query performance

3. **Advanced Features:**
   - Add pagination support
   - Implement query optimization
   - Add database migrations (Flyway/Liquibase)
   - Add audit logging

4. **Testing:**
   - Add integration tests with TestContainers
   - Add performance tests
   - Test concurrent operations

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Ensure PostgreSQL is running on localhost:5432 |
| Database does not exist | Run: `CREATE DATABASE order_db;` |
| Tables not created | Enable `ddl-auto: create` or run schema.sql |
| Port already in use | Change `server.port` in application.yaml |
| Permission denied | Check PostgreSQL user permissions |

