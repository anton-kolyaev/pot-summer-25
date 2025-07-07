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
```bash
docker compose ps
```

The database will be available at `localhost:5432` with the following credentials:
- **Database:** `insurance_service`
- **Username:** `insurance_app`
- **Password:** `insurance_app_password`

## Database Configuration

The PostgreSQL container is configured with:
- **Image:** postgres:16.9
- **Port:** 5432
- **Data persistence:** Docker volume `postgres_data`
- **Initialization:** SQL scripts in `docker/postgres/init/`

## Useful Commands

- **Start database:** `docker compose up -d postgres`
- **Stop database:** `docker compose down`
- **View logs:** `docker compose logs postgres`
- **Reset database:** `docker compose down -v && docker compose up -d postgres` 