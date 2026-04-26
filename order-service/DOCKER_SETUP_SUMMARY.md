# Docker Compose Setup Summary

## ✅ Setup Complete!

Your Order Service microservice is now fully configured with Docker Compose for containerized deployment.

---

## 📦 Files Created/Updated

### New Files
| File | Purpose |
|------|---------|
| **docker-compose.yml** | Multi-container orchestration configuration |
| **.env** | Environment variables for configuration |
| **DOCKER_COMPOSE_GUIDE.md** | Comprehensive Docker Compose documentation |

### Updated Files
| File | Changes |
|------|---------|
| **application.yaml** | Added environment variable support |
| **README.md** | Added Docker Compose quick start section |

---

## 🚀 Quick Start

### 1. Build the Application
```bash
cd /Users/kishorevanam/git/microservice-design-patterns/order-service
./mvnw clean package
```

### 2. Start Services
```bash
docker-compose up -d
```

### 3. Verify Services Running
```bash
docker-compose ps
```

### 4. Access Application
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/api/orders/health
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────┐
│         Docker Compose Network              │
├─────────────────────────────────────────────┤
│                                             │
│  PostgreSQL (postgres:16-alpine)            │
│  ├─ Port: 5432                              │
│  ├─ Database: order_db                      │
│  ├─ User: kishorevanam                      │
│  └─ Volume: postgres_data (persistent)      │
│           ↑                                  │
│           │ (internal network: postgres)    │
│           ↓                                  │
│  Order Service (eclipse-temurin:21)         │
│  ├─ Port: 8080                              │
│  ├─ Depends on: postgres health check       │
│  └─ Restart: unless-stopped                 │
│                                             │
└─────────────────────────────────────────────┘
```

---

## 📋 Service Configuration

### PostgreSQL Service
- **Image**: postgres:16-alpine (lightweight, ~180MB)
- **Container Name**: order-service-postgres
- **Port**: 5432
- **Database**: order_db
- **Credentials**: kishorevanam / kishorevanam
- **Volume**: postgres_data (data persists across restarts)
- **Health Check**: Enabled (waits for DB to be ready before starting app)
- **Initialization**: Loads schema.sql automatically

### Order Service Application
- **Image**: Built from Dockerfile (eclipse-temurin:21)
- **Container Name**: order-service-app
- **Port**: 8080
- **Dependencies**: PostgreSQL (waits for health check)
- **Restart Policy**: unless-stopped (auto-restarts on failure)
- **Configuration**: Environment variables from .env file

---

## 🔧 Environment Configuration

### .env File
Located at project root: `/Users/kishorevanam/git/microservice-design-patterns/order-service/.env`

```env
# PostgreSQL Configuration
POSTGRES_DB=order_db
POSTGRES_USER=kishorevanam
POSTGRES_PASSWORD=kishorevanam

# Spring Boot Database Configuration
SPRING_DATASOURCE_URL=postgres:5432/order_db
SPRING_DATASOURCE_USERNAME=kishorevanam
SPRING_DATASOURCE_PASSWORD=kishorevanam
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Spring Boot Configuration
SPRING_APPLICATION_NAME=order-service
SERVER_PORT=8080
```

### application.yaml Changes
Updated to support environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${SPRING_DATASOURCE_URL:localhost:5432/order_db}
    username: ${SPRING_DATASOURCE_USERNAME:kishorevanam}
    password: ${SPRING_DATASOURCE_PASSWORD:kishorevanam}
```

This allows:
- ✅ Works with Docker Compose (uses service name "postgres")
- ✅ Works locally (falls back to "localhost:5432")
- ✅ Easy to customize via .env file

---

## 📝 Common Commands

### Start Services
```bash
# Start in background
docker-compose up -d

# Start with logs visible
docker-compose up

# Start and rebuild images
docker-compose up -d --build
```

### View Status & Logs
```bash
# Show running containers
docker-compose ps

# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f postgres
docker-compose logs -f order-service

# View last 100 lines
docker-compose logs --tail=100
```

### Stop Services
```bash
# Stop (containers kept, volumes persist)
docker-compose stop

# Stop and remove containers (volumes persist)
docker-compose down

# Stop, remove containers and volumes (complete cleanup)
docker-compose down -v
```

### Execute Commands
```bash
# PostgreSQL CLI
docker-compose exec postgres psql -U kishorevanam -d order_db

# View PostgreSQL version
docker-compose exec postgres psql -U kishorevanam -c "SELECT version();"

# List all databases
docker-compose exec postgres psql -U kishorevanam -l

# List tables in order_db
docker-compose exec postgres psql -U kishorevanam -d order_db -c "\dt"

# Order Service bash
docker-compose exec order-service bash

# Check Spring Boot logs inside container
docker-compose exec order-service tail -f /var/log/app.log
```

### Rebuild & Restart
```bash
# Rebuild application after code changes
./mvnw clean package

# Rebuild Docker image and restart
docker-compose up -d --build order-service
```

### Data Management
```bash
# Backup PostgreSQL data
docker-compose exec postgres pg_dump -U kishorevanam order_db > backup.sql

# Restore PostgreSQL data
docker-compose exec -T postgres psql -U kishorevanam order_db < backup.sql

# Remove volumes (full cleanup)
docker volume rm order-service_postgres_data
```

---

## ✨ Key Features Implemented

✅ **Multi-Container Orchestration** - Docker Compose manages both services
✅ **Official PostgreSQL Image** - Using postgres:16-alpine (lightweight)
✅ **Health Checks** - PostgreSQL health verified before starting app
✅ **Data Persistence** - Named volume keeps data across restarts
✅ **Environment Variables** - Easy configuration via .env file
✅ **Internal Networking** - Services communicate via bridge network
✅ **Auto-Restart** - Services restart automatically on failure
✅ **Schema Initialization** - schema.sql loaded automatically on startup
✅ **Development Ready** - Optimized for local development workflow

---

## 🔄 Development Workflow

### Option 1: Full Docker Development (Recommended)
```bash
# Build application
./mvnw clean package

# Start all services
docker-compose up -d

# Access Swagger UI
# http://localhost:8080/swagger-ui.html

# View logs
docker-compose logs -f

# Stop when done
docker-compose down
```

### Option 2: Mixed Setup (Local App + Docker DB)
```bash
# Start PostgreSQL only
docker-compose up -d postgres

# Run application locally
./mvnw spring-boot:run

# Application connects to Docker PostgreSQL on localhost:5432
```

### Option 3: Local Development (Everything Local)
```bash
# Run PostgreSQL locally (if installed)
psql -U postgres -c "CREATE DATABASE order_db;"

# Start application
./mvnw spring-boot:run
```

---

## 🔗 Service Communication

### From Container to Container
Order Service connects to PostgreSQL using:
```
jdbc:postgresql://postgres:5432/order_db
```
(Uses service name "postgres" as hostname on internal network)

### From Localhost/Local Tools
PostgreSQL accessible using:
```
Host: localhost
Port: 5432
Database: order_db
Username: kishorevanam
Password: kishorevanam
```

Example with psql:
```bash
psql -h localhost -U kishorevanam -d order_db
```

---

## 📊 Docker Compose Features Used

| Feature | Purpose |
|---------|---------|
| **env_file** | Load environment variables from .env |
| **environment** | Override environment variables |
| **depends_on** | Control startup order |
| **condition: service_healthy** | Wait for health check to pass |
| **healthcheck** | Define service health criteria |
| **ports** | Expose container ports to localhost |
| **volumes** | Persist data across restarts |
| **networks** | Internal service communication |
| **restart_policy** | Auto-restart on failure |

---

## ⚠️ Troubleshooting

### Services won't start
```bash
# Check syntax
docker-compose config

# View detailed error logs
docker-compose logs

# Rebuild without cache
docker-compose build --no-cache
```

### Port already in use
```bash
# Find what's using port
lsof -i :5432
lsof -i :8080

# Change port in docker-compose.yml
# From: - "5432:5432"
# To:   - "5433:5432"
```

### Database connection timeout
```bash
# Ensure postgres is healthy
docker-compose ps

# Check postgres logs
docker-compose logs postgres

# Restart postgres
docker-compose restart postgres
```

### Application can't connect to database
```bash
# Check network connectivity
docker-compose exec order-service ping postgres

# Verify SPRING_DATASOURCE_URL environment variable
docker-compose exec order-service env | grep SPRING
```

### Lost data after stopping containers
The docker-compose.yml uses a **named volume** (postgres_data) which **persists data**:
```bash
# Data persists with:
docker-compose down

# Data is LOST with:
docker-compose down -v
```

---

## 🔐 Security Notes

⚠️ **For Development**: Current configuration is safe for local development

⚠️ **For Production**, implement:
- [ ] Use strong passwords in .env
- [ ] Never commit .env with real credentials
- [ ] Use Docker secrets or external secret management
- [ ] Enable SSL/TLS for database connections
- [ ] Implement resource limits (cpu, memory)
- [ ] Use private Docker registry
- [ ] Implement authentication on API
- [ ] Add API rate limiting
- [ ] Enable logging and monitoring

---

## 📚 Additional Resources

- **Docker Compose Guide**: [DOCKER_COMPOSE_GUIDE.md](DOCKER_COMPOSE_GUIDE.md)
- **Project README**: [README.md](README.md)
- **Docker Documentation**: https://docs.docker.com/compose/
- **PostgreSQL Image**: https://hub.docker.com/_/postgres

---

## ✅ Next Steps

1. **Build the Application**
   ```bash
   ./mvnw clean package
   ```

2. **Start Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access Swagger UI**
   ```
   http://localhost:8080/swagger-ui.html
   ```

4. **Test API Endpoints**
   - Create an order
   - Get all orders
   - Update order
   - Delete order

5. **Monitor Logs**
   ```bash
   docker-compose logs -f
   ```

---

## 📞 Support

For detailed Docker Compose documentation, see [DOCKER_COMPOSE_GUIDE.md](DOCKER_COMPOSE_GUIDE.md)

For general project documentation, see [README.md](README.md)


