#!/bin/bash

echo "Stopping and removing PostgreSQL container..."
docker compose down -v

echo "Starting PostgreSQL container..."
docker compose up -d postgres

echo "Waiting for PostgreSQL to be ready..."
until docker compose exec postgres pg_isready -U postgres; do
  echo "PostgreSQL is starting up..."
  sleep 2
done

echo "PostgreSQL is ready!"
echo "Database: insurance_service"
echo "Username: insurance_app"
echo "Password: insurance_app_password" 