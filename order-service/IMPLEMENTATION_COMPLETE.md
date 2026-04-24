# Order Service - PostgreSQL Implementation Guide

## Summary

The Order Service has been successfully upgraded with **PostgreSQL database connectivity** and now supports **full CRUD operations** with proper data persistence, relationships, and transaction management.

## What Has Been Implemented

### ✅ Database Integration
- Spring Data JPA with Hibernate ORM
- PostgreSQL JDBC driver
- Automatic table creation via Hibernate
- Database initialization script

### ✅ Entity Models
- **Order** - Main order entity with relationships
- **OrderItem** - Order line items with foreign key to Order
- Automatic timestamp management (createdAt, updatedAt)
- Cascade delete support

### ✅ Repository Layer
- Spring Data JPA repositories
- Custom query methods for filtering:
  - `findByCustomerId()` - Find orders by customer
  - `findByStatus()` - Find orders by status
  - `findByOrderNumber()` - Find order by unique number

### ✅ Service Layer
- Transaction management with @Transactional
- Business logic for all CRUD operations
- Price calculation
- Order number generation
- Entity-to-DTO conversion

### ✅ API Endpoints
All REST endpoints with database persistence:
- ✅ POST `/api/orders` - Create order
- ✅ GET `/api/orders` - Get all orders
- ✅ GET `/api/orders?customerId=X` - Filter by customer
- ✅ GET `/api/orders?status=X` - Filter by status
- ✅ GET `/api/orders/{id}` - Get single order
- ✅ PUT `/api/orders/{id}` - Update order
- ✅ DELETE `/api/orders/{id}` - Delete order
- ✅ GET `/api/orders/health` - Health check

### ✅ Database Schema
- Tables with proper constraints
- Foreign key relationships
- Indexes for performance
- Timestamp columns

## Quick Setup Instructions

### Step 1: Create PostgreSQL Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE order_db;

# Exit
\q
```

### Step 2: Build the Application

```bash
cd /Users/kishorevanam/git/microservice-design-patterns/order-service

# Build (skip tests for faster build)
./mvnw clean package -DskipTests

# Or compile only
./mvnw clean compile
```

### Step 3: Run the Application

```bash
# Option 1: Maven
./mvnw spring-boot:run

# Option 2: Java JAR (after build)
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

The application starts on **http://localhost:8080**

### Step 4: Test the API

```bash
# Health check
curl http://localhost:8080/api/orders/health

# Create an order
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
curl http://localhost:8080/api/orders

# Filter by customer
curl "http://localhost:8080/api/orders?customerId=CUST001"

# Filter by status
curl "http://localhost:8080/api/orders?status=PENDING"
```

## Database Credentials

**Default Configuration (in `application.yaml`):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
    username: postgres
    password: postgres
```

If your PostgreSQL uses different credentials, update `src/main/resources/application.yaml`

## Files Created/Modified

### New Files Created
```
src/main/resources/
  └── schema.sql                    # Database schema (optional - Hibernate auto-creates)

repository/
  └── OrderItemRepository.java      # Spring Data JPA repository for OrderItems

Documentation files:
  ├── DATABASE_SETUP.md             # Detailed database setup guide
  ├── QUICK_START.md                # Quick start guide
  └── POSTGRESQL_INTEGRATION.md     # Comprehensive integration details
```

### Files Modified
```
entity/
  ├── Order.java                    # Added JPA annotations, relationships
  └── OrderItem.java                # Added JPA annotations, foreign key

repository/
  └── OrderRepository.java          # Changed to Spring Data JPA interface

service/
  └── OrderService.java             # Added transaction management, new filter methods

controller/
  └── OrderController.java          # Enhanced with query parameter filtering

pom.xml                             # Added dependencies for JPA, JDBC, PostgreSQL

application.yaml                    # Added database configuration
```

## Key Dependencies Added

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- JDBC Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
    <scope>runtime</scope>
</dependency>
```

## Database Schema Overview

### orders table
```
┌─────────────────┬──────────────────────────┐
│ id              │ BIGSERIAL PRIMARY KEY    │
│ order_number    │ VARCHAR(255) UNIQUE      │
│ status          │ VARCHAR(50)              │
│ customer_id     │ VARCHAR(255)             │
│ total_price     │ DECIMAL(10, 2)           │
│ created_at      │ TIMESTAMP (auto)         │
│ updated_at      │ TIMESTAMP (auto)         │
└─────────────────┴──────────────────────────┘
Indexes: order_number, customer_id, status
```

### order_items table
```
┌─────────────────┬──────────────────────────┐
│ id              │ BIGSERIAL PRIMARY KEY    │
│ order_id        │ BIGINT (FK → orders.id)  │
│ product_id      │ VARCHAR(255)             │
│ product_name    │ VARCHAR(255)             │
│ quantity        │ INTEGER                  │
│ unit_price      │ DECIMAL(10, 2)           │
│ total_price     │ DECIMAL(10, 2)           │
└─────────────────┴──────────────────────────┘
Indexes: order_id (Foreign Key)
Cascade: ON DELETE CASCADE (delete items when order deleted)
```

## Transaction Flow Example

**Creating an Order:**
```
1. Client sends POST /api/orders request
2. OrderController receives OrderDTO
3. OrderService.createOrder() starts transaction
4. Order entity saved to database
5. OrderItem entities saved (cascade)
6. Transaction committed
7. Response returned with generated IDs
```

## Performance Features

1. **Connection Pooling** - HikariCP (auto-configured)
2. **Lazy Loading** - OrderItems loaded only when accessed
3. **Indexes** - On frequently queried columns
4. **Query Optimization** - Spring Data JPA generates efficient SQL
5. **Caching Ready** - Can be added with @Cacheable

## Troubleshooting

| Problem | Solution |
|---------|----------|
| **Connection refused** | Ensure PostgreSQL is running: `brew services list` or `pg_isready` |
| **Database does not exist** | Create it: `psql -U postgres -c "CREATE DATABASE order_db;"` |
| **Wrong credentials** | Update `application.yaml` with correct username/password |
| **Port 8080 in use** | Change `server.port` in `application.yaml` |
| **Tables not created** | They auto-create, or manually run `schema.sql` |

## Verification Steps

### 1. Database Connection
```bash
psql -U postgres -d order_db -c "\dt"
# Should show: orders, order_items tables
```

### 2. Application Health
```bash
curl http://localhost:8080/api/orders/health
# Should return: Order Service is running
```

### 3. Database Operation
```bash
# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"TEST","items":[{"productId":"P1","productName":"Test","quantity":1,"unitPrice":100}]}'

# Verify in database
psql -U postgres -d order_db -c "SELECT * FROM orders;"
```

## Next Steps (Optional)

1. **Add Pagination:**
   - Use Spring Data's `Pageable`
   - Modify repository to `extends PagingAndSortingRepository`

2. **Add Validation:**
   - Add `@Valid` and Bean Validation annotations
   - Handle validation exceptions

3. **Add Auditing:**
   - Use `@CreatedBy`, `@LastModifiedBy`
   - Track who created/modified orders

4. **Add Caching:**
   - Add `@Cacheable` to frequently accessed queries
   - Use Redis for distributed caching

5. **Add Migrations:**
   - Use Flyway or Liquibase
   - Version database schema changes

6. **Add Security:**
   - Implement Spring Security
   - Add authentication/authorization

## Project Structure

```
order-service/
├── src/main/java/com/kish/mcdp/
│   ├── entity/
│   │   ├── Order.java              (JPA Entity)
│   │   └── OrderItem.java          (JPA Entity)
│   ├── dto/
│   │   ├── OrderDTO.java
│   │   └── OrderItemDTO.java
│   ├── repository/
│   │   ├── OrderRepository.java    (Spring Data JPA)
│   │   └── OrderItemRepository.java (Spring Data JPA)
│   ├── service/
│   │   ├── OrderService.java       (@Transactional)
│   │   └── OrderServiceTest.java
│   ├── controller/
│   │   └── OrderController.java    (REST Endpoints)
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   └── OrderServiceApplication.java
├── src/main/resources/
│   ├── application.yaml            (Database config)
│   └── schema.sql                  (Optional schema)
├── pom.xml                         (Dependencies)
├── README.md
├── QUICK_START.md
├── DATABASE_SETUP.md
└── POSTGRESQL_INTEGRATION.md
```

## Build Status

✅ **Project compiles successfully**
✅ **All dependencies resolved**
✅ **No compilation errors**
✅ **Ready for deployment**

## Contact & Support

For issues or questions, refer to:
- [QUICK_START.md](QUICK_START.md) - Quick setup
- [DATABASE_SETUP.md](DATABASE_SETUP.md) - Database help
- [POSTGRESQL_INTEGRATION.md](POSTGRESQL_INTEGRATION.md) - Technical details
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - API reference

