# Docker Setup for Insurance Service

This directory contains Docker configuration for running the Insurance Service application locally.

## Prerequisites

- Docker
- Docker Compose

## Quick Start

1. **Start the PostgreSQL database:**
   ```bash
   docker compose up -d postgres
   ```

2. **Wait for the database to be ready:**
   The health check will ensure PostgreSQL is fully initialized before accepting connections.

3. **Run the Spring Boot application:**
   ```bash
   ./gradlew bootRun
   ```

## Database Configuration

The PostgreSQL database is configured with the following settings:

- **Host:** localhost
- **Port:** 5432
- **Database:** insurance_service
- **Username:** insurance_app
- **Password:** insurance_app_password

## Initialization Scripts

The PostgreSQL container automatically runs initialization scripts in order:

1. **`docker/postgres/init/01-init-database.sql`** - Creates the `insurance_service` database and application user with appropriate privileges
2. **`docker/postgres/init/02-create-schema.sql`** - Creates all the database tables, indexes, and initial data for the insurance service application

The schema includes:
- Companies, Users, and Roles management
- Insurance and Benefit packages
- User enrollments and claims
- Audit logging
- Comprehensive indexing for performance
- Default role definitions

## Useful Commands

**Start all services:**
```bash
docker compose up -d
```

**Stop all services:**
```bash
docker compose down
```

**View logs:**
```bash
docker compose logs postgres
```

**Reset database (removes all data):**
```bash
docker compose down -v
docker compose up -d postgres
```

**Connect to database directly:**
```bash
docker exec -it insurance-service-postgres psql -U postgres -d insurance_service
```

## Troubleshooting

If you encounter connection issues:
1. Ensure the PostgreSQL container is running: `docker-compose ps`
2. Check the logs: `docker-compose logs postgres`
3. Verify the health check passed: `docker-compose ps` should show "healthy" status 