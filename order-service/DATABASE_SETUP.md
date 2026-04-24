# PostgreSQL Database Setup Guide

## Prerequisites
- PostgreSQL installed and running locally on port 5432
- psql command-line tool available

## Steps to Set Up the Database

### 1. Create the Database

Open a terminal and connect to PostgreSQL:

```bash
psql -U postgres
```

Then create the database:

```sql
CREATE DATABASE order_db;
```

### 2. Create Tables and Schema

Connect to the new database:

```bash
psql -U postgres -d order_db
```

Run the SQL script to create tables:

```bash
psql -U postgres -d order_db -f src/main/resources/schema.sql
```

Or manually execute the SQL from `schema.sql` in your PostgreSQL client.

### 3. Verify the Tables

Connect to the database and verify the tables were created:

```bash
psql -U postgres -d order_db
```

List tables:

```sql
\dt
```

You should see:
- `orders` table
- `order_items` table

### 4. Alternative: Using pgAdmin

If you prefer a GUI:

1. Open pgAdmin
2. Create a new database named `order_db`
3. Right-click on the database and select "Query Tool"
4. Copy and paste the contents of `schema.sql`
5. Execute the query

## Connection Configuration

The application uses the following default connection parameters (in `application.yaml`):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/order_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

If your PostgreSQL setup uses different credentials, update the `application.yaml` file.

## Database Reset

To reset the database and start fresh:

```bash
psql -U postgres -d order_db
```

Drop the tables:

```sql
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
```

Then re-run the `schema.sql` script.

## Troubleshooting

### Connection Refused
- Ensure PostgreSQL is running on localhost:5432
- Check credentials in `application.yaml`

### Database Does Not Exist
- Create the database using the steps above
- Ensure the database name matches in `application.yaml`

### Tables Not Created
- Run the `schema.sql` script manually
- Check for SQL syntax errors

## Testing the Connection

Once the application is running, test the health endpoint:

```bash
curl http://localhost:8080/api/orders/health
```

Expected response:
```
Order Service is running
```

Then test creating an order to ensure the database connection is working:

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

