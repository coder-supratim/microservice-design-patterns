# Swagger/OpenAPI Documentation Guide

## Overview

The Order Service now includes comprehensive **Swagger/OpenAPI documentation** using **Springdoc OpenAPI**. This provides interactive API documentation with a built-in UI for testing endpoints.

## Accessing Swagger UI

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

## API Documentation URL

The OpenAPI specification (JSON format) is available at:

```
http://localhost:8080/v3/api-docs
```

YAML format:
```
http://localhost:8080/v3/api-docs.yaml
```

## Quick Start

### 1. Start the Application
```bash
cd /Users/kishorevanam/git/microservice-design-patterns/order-service
./mvnw spring-boot:run
```

### 2. Open Swagger UI
Navigate to: http://localhost:8080/swagger-ui.html

### 3. Try API Endpoints
- Expand any endpoint in the UI
- Click "Try it out"
- Fill in parameters
- Click "Execute"

## Features

### ✅ Implemented Documentation

**Controller Level (@Tag)**
- Group operations by logical units
- Provide operation descriptions
- Include request/response examples

**Endpoint Level (@Operation)**
- Detailed operation descriptions
- Parameter documentation
- Response descriptions
- Error code documentation

**Request/Response Level (@Schema)**
- Data type documentation
- Field descriptions
- Required field indicators
- Example values
- Validation constraints

### ✅ API Documentation Details

#### Order Management Tag
All order-related endpoints are grouped under "Order Management"

#### Endpoints Documented

1. **Create Order**
   - Summary: Create a new order
   - Description: Creates a new order with customer ID and items
   - Request body: OrderDTO with example
   - Response: 201 Created with OrderDTO

2. **Get Order by ID**
   - Summary: Get order by ID
   - Description: Retrieves specific order
   - Path parameter: id with example
   - Response: 200 OK with OrderDTO

3. **Get All Orders**
   - Summary: Get all orders with optional filtering
   - Description: Retrieves all or filtered orders
   - Query parameters: customerId, status
   - Response: 200 OK with OrderDTO list

4. **Update Order**
   - Summary: Update existing order
   - Description: Updates status and/or items
   - Path parameter: id
   - Request body: OrderDTO
   - Response: 200 OK with updated OrderDTO

5. **Delete Order**
   - Summary: Delete an order
   - Description: Deletes order and associated items
   - Path parameter: id
   - Response: 204 No Content

6. **Health Check**
   - Summary: Health check endpoint
   - Description: Checks if service is running
   - Response: 200 OK

## Data Models Documentation

### OrderDTO
- **id**: Unique order identifier (read-only)
- **orderNumber**: Unique order number (read-only, auto-generated)
- **status**: Order status with allowed values
- **customerId**: Customer identifier (required)
- **totalPrice**: Total order price (read-only, auto-calculated)
- **items**: List of order items (required)
- **createdAt**: Creation timestamp (read-only)
- **updatedAt**: Last update timestamp (read-only)

### OrderItemDTO
- **id**: Unique item identifier (read-only)
- **productId**: Product identifier (required)
- **productName**: Product name (required)
- **quantity**: Item quantity >= 1 (required)
- **unitPrice**: Price per unit >= 0 (required)
- **totalPrice**: Total price (read-only, auto-calculated)

## HTTP Status Codes Documented

| Code | Meaning | Endpoints |
|------|---------|-----------|
| 200 | OK | GET, PUT |
| 201 | Created | POST |
| 204 | No Content | DELETE |
| 400 | Bad Request | POST, PUT |
| 404 | Not Found | GET, PUT, DELETE |
| 500 | Server Error | All |

## Example Usage via Swagger UI

### Creating an Order
1. Go to http://localhost:8080/swagger-ui.html
2. Expand "Order Management" section
3. Expand "POST /api/orders"
4. Click "Try it out"
5. Fill in request body:
```json
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
6. Click "Execute"
7. See response with generated ID and order number

### Getting All Orders
1. Expand "GET /api/orders"
2. Click "Try it out"
3. Optionally enter query parameters:
   - customerId: CUST001
   - status: PENDING
4. Click "Execute"

## Server Configuration

The Swagger documentation includes server configuration:

```yaml
servers:
  - url: http://localhost:8080
    description: Local Development Server
```

You can add additional servers in `OpenAPIConfig.java`:

```java
new Server()
    .url("https://production.example.com")
    .description("Production Server")
```

## Generated OpenAPI Specification

The complete OpenAPI 3.0 specification is automatically generated and available at:

**JSON:** `http://localhost:8080/v3/api-docs`
**YAML:** `http://localhost:8080/v3/api-docs.yaml`

Example YAML excerpt:
```yaml
openapi: 3.0.1
info:
  title: Order Service API
  description: REST API for managing orders...
  version: 1.0.0
servers:
  - url: http://localhost:8080
    description: Local Development Server
paths:
  /api/orders:
    post:
      tags:
        - Order Management
      summary: Create a new order
      description: Creates a new order with the provided...
      requestBody:
        description: Order details to create
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/OrderDTO'
      responses:
        '201':
          description: Order created successfully
```

## Integration with Other Tools

### Postman
1. Go to http://localhost:8080/v3/api-docs
2. Copy the JSON
3. In Postman: File → Import → Paste Raw Text
4. Create collection from OpenAPI spec

### Insomnia
1. Create new collection
2. Import from URL: http://localhost:8080/v3/api-docs
3. Automatically generates all endpoints

### API Client Generators
Generate client libraries using openapi-generator:

```bash
# Generate Java client
openapi-generator generate \
  -i http://localhost:8080/v3/api-docs \
  -g java \
  -o ./generated-client

# Generate TypeScript client
openapi-generator generate \
  -i http://localhost:8080/v3/api-docs \
  -g typescript-axios \
  -o ./generated-client
```

## Customization

### Add Custom Response Schemas
In `GlobalExceptionHandler.java`:

```java
@Schema(name = "ErrorResponse", description = "Error response object")
public class ErrorResponse {
    @Schema(description = "Error message")
    private String message;
    // ...
}
```

### Add API Key Authentication
In `OpenAPIConfig.java`:

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("api-key", 
                new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-API-Key")))
        .info(new Info()...);
}
```

## Documentation Files Created

- **OpenAPIConfig.java** - Swagger/OpenAPI configuration
- **OrderDTO.java** - Updated with @Schema annotations
- **OrderItemDTO.java** - Updated with @Schema annotations
- **OrderController.java** - Updated with comprehensive annotations

## Configuration in application.yaml

Optional: Add these properties to customize Swagger UI:

```yaml
springdoc:
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    show-common-extensions: true
  api-docs:
    path: /v3/api-docs
```

## Testing Workflow

### 1. Manual Testing via Swagger UI
- Go to http://localhost:8080/swagger-ui.html
- Try each endpoint
- See live responses

### 2. Export and Use
- Export OpenAPI JSON for documentation
- Use for client generation
- Import to Postman/Insomnia

### 3. CI/CD Integration
- Validate OpenAPI spec in build
- Generate client SDKs automatically
- Test API compatibility

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Swagger UI not loading | Check application is running, visit http://localhost:8080/swagger-ui.html |
| Missing endpoints in Swagger | Ensure @RestController and @RequestMapping are present |
| Bad OpenAPI spec | Check annotations for typos, validate at https://editor.swagger.io/ |
| Deprecated API warnings | These are normal; suppress with compiler flags if needed |

## Benefits

✅ **Interactive Documentation** - Try endpoints directly in browser
✅ **Auto-generated** - Always synced with actual API
✅ **Client Generation** - Generate SDKs for multiple languages
✅ **Testing** - Built-in request/response testing
✅ **Professional** - Provides professional API documentation
✅ **Standardized** - OpenAPI 3.0 standard format
✅ **Team Collaboration** - Easy to share API documentation

## Next Steps

1. **Visit Swagger UI** - http://localhost:8080/swagger-ui.html
2. **Test an Endpoint** - Create an order via Swagger UI
3. **Export Spec** - Get the OpenAPI JSON for your documentation
4. **Share with Team** - Provide Swagger UI URL to your team
5. **Generate Clients** - Use openapi-generator for client libraries

## References

- [Springdoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.0)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Generator](https://openapi-generator.tech/)

