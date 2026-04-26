# Docker Compose Setup Guide

## Overview

This project uses Docker Compose to orchestrate multi-container deployment with PostgreSQL database and Order Service application.

## Prerequisites

- **Docker Desktop** - Download from https://www.docker.com/products/docker-desktop
- **Docker Compose** - Included with Docker Desktop
- **No need for local PostgreSQL installation**

## Files Created

| File | Purpose |
|------|---------|
| `docker-compose.yml` | Docker Compose configuration for all services |
| `.env` | Environment variables for configuration |
| `Dockerfile` | Order Service container image |
| `application.yaml` | Updated to support environment variables |

## Architecture

```
┌─────────────────────────────────────────┐
│         Docker Network (order-network)   │
├─────────────────────────────────────────┤
│                                          │
│  ┌──────────────────────┐                │
│  │ PostgreSQL Service   │                │
│  │ postgres:16-alpine   │                │
│  │                      │                │
│  │ Port: 5432           │                │
│  │ Container: order-    │                │
│  │ service-postgres     │                │
│  └──────────────────────┘                │
│           ↑                              │
│           │ (internal network)           │
│           ↓                              │
│  ┌──────────────────────┐                │
│  │ Order Service App    │                │
│  │ eclipse-temurin:21   │                │
│  │                      │                │
│  │ Port: 8080           │                │
│  │ Container: order-    │                │
│  │ service-app          │                │
│  └──────────────────────┘                │
│                                          │
└─────────────────────────────────────────┘
```

## Quick Start

### 1. Build the Application

```bash
cd /Users/kishorevanam/git/microservice-design-patterns/order-service
./mvnw clean package
```

### 2. Start All Services

```bash
docker-compose up -d
```

The `-d` flag runs services in background (detached mode).

### 3. Verify Services are Running

```bash
docker-compose ps
```

Expected output:
```
NAME                        STATUS              PORTS
order-service-app          Up (healthy)        0.0.0.0:8080->8080/tcp
order-service-postgres     Up (healthy)        0.0.0.0:5432->5432/tcp
```

### 4. Access the Application

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/api/orders/health
- **Database**: localhost:5432 (from local tools like pgAdmin)

## Common Commands

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f postgres
docker-compose logs -f order-service
```

### Stop Services

```bash
# Stop without removing
docker-compose stop

# Stop and remove containers (data persists in volumes)
docker-compose down

# Stop and remove everything including volumes
docker-compose down -v
```

### Restart Services

```bash
docker-compose restart
```

### Rebuild Images

```bash
docker-compose build --no-cache
```

### Execute Commands in Container

```bash
# PostgreSQL CLI
docker-compose exec postgres psql -U kishorevanam -d order_db

# Order Service bash
docker-compose exec order-service bash
```

## Environment Configuration

### Using .env File

Edit `.env` file to customize:

```env
# PostgreSQL
POSTGRES_DB=order_db
POSTGRES_USER=kishorevanam
POSTGRES_PASSWORD=kishorevanam

# Spring Boot
SPRING_DATASOURCE_URL=postgres:5432/order_db
SPRING_DATASOURCE_USERNAME=kishorevanam
SPRING_DATASOURCE_PASSWORD=kishorevanam
```

### Override at Runtime

```bash
# Using environment variables
docker-compose -e POSTGRES_PASSWORD=newpassword up -d

# Using command line
docker-compose up -e POSTGRES_PASSWORD=newpassword -d
```

## Service Details

### PostgreSQL Service

- **Image**: postgres:16-alpine (lightweight, ~180MB)
- **Container Name**: order-service-postgres
- **Port**: 5432 (accessible from localhost)
- **Database**: order_db
- **Credentials**: kishorevanam / kishorevanam
- **Volume**: postgres_data (persistent data storage)
- **Health Check**: Enabled (waits for DB to be ready)
- **Initialization**: Runs schema.sql on startup

**Connection from Local Tools**:
```
Host: localhost
Port: 5432
Database: order_db
Username: kishorevanam
Password: kishorevanam
```

### Order Service

- **Image**: Built from Dockerfile (eclipse-temurin:21)
- **Container Name**: order-service-app
- **Port**: 8080 (accessible from localhost)
- **Depends On**: PostgreSQL (waits for health check)
- **Environment**: Loads from .env file
- **Restart Policy**: unless-stopped (auto-restart on failure)

## Database Initialization

The `schema.sql` file is automatically run when PostgreSQL starts:

```bash
- ./src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql
```

This creates all necessary tables on first run.

## Data Persistence

PostgreSQL data is persisted in a named volume:

```bash
# View volumes
docker volume ls | grep postgres_data

# Inspect volume
docker volume inspect order-service_postgres_data

# Clean volume
docker volume rm order-service_postgres_data
```

Even after `docker-compose down`, data persists. Remove with `docker-compose down -v`.

## Networking

Services communicate via internal Docker network `order-network`:

- Order Service connects to PostgreSQL using: `jdbc:postgresql://postgres:5432/order_db`
- From localhost, use: `jdbc:postgresql://localhost:5432/order_db`

## Troubleshooting

### Application won't connect to PostgreSQL

```bash
# Check PostgreSQL is healthy
docker-compose logs postgres

# Check connection inside container
docker-compose exec order-service ping postgres

# Verify network connectivity
docker network inspect order-service_order-network
```

### Port 5432 or 8080 already in use

```bash
# Find process using port
lsof -i :5432
lsof -i :8080

# Change port in docker-compose.yml
# Before:  - "5432:5432"
# Change to:  - "5433:5432"
```

### PostgreSQL won't start

```bash
# Remove old volume and restart
docker-compose down -v
docker-compose up -d postgres --wait
```

### Application container exits immediately

```bash
# Check logs
docker-compose logs order-service

# Rebuild without cache
docker-compose build --no-cache order-service
docker-compose up order-service
```

### Database connection timeout

Ensure PostgreSQL health check passes:
```bash
docker-compose logs postgres | grep healthcheck
```

## Development Workflow

### 1. Local Development
```bash
# Run application locally with Docker PostgreSQL
docker-compose up postgres
./mvnw spring-boot:run
```

### 2. Full Docker Development
```bash
# Run everything in containers
docker-compose up
```

### 3. After Code Changes
```bash
# Rebuild and restart
./mvnw clean package
docker-compose up -d --build order-service
```

## Production Considerations

⚠️ **Security**: The `.env` file contains credentials. For production:

```bash
# Use Docker secrets
echo "mypassword" | docker secret create db_password -

# Or use .env.prod (never commit sensitive files)
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

**Other Production Notes**:
- Change default credentials in `.env`
- Use strong passwords
- Implement database backups
- Use resource limits (cpu, memory)
- Enable logging aggregation
- Use private Docker registry
- Implement health checks on web service

## Example: Complete Workflow

```bash
# 1. Start services
cd /Users/kishorevanam/git/microservice-design-patterns/order-service
./mvnw clean package
docker-compose up -d

# 2. Wait for services to be ready (auto-waits with health checks)
sleep 10

# 3. Create an order via Swagger UI
# Visit: http://localhost:8080/swagger-ui.html

# 4. Query database directly
docker-compose exec postgres psql -U kishorevanam -d order_db -c "SELECT * FROM orders;"

# 5. View logs
docker-compose logs -f order-service

# 6. Stop and cleanup
docker-compose down -v
```

## Useful Docker Compose Features Used

✅ **Health Checks** - PostgreSQL container waits until database is ready
✅ **Dependency Management** - Order Service waits for PostgreSQL health check
✅ **Named Volumes** - Data persists across container restarts
✅ **Environment Variables** - Easy configuration via .env file
✅ **Custom Networks** - Services communicate on internal bridge network
✅ **Volume Mounts** - schema.sql automatically loaded on startup
✅ **Restart Policies** - Auto-restart on failure
✅ **Port Mapping** - Access services from localhost

## Useful Links

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Docker Official Images](https://hub.docker.com/_/postgres)
- [PostgreSQL Image Documentation](https://hub.docker.com/_/postgres)
- [Eclipse Temurin Images](https://hub.docker.com/_/eclipse-temurin)


