#!/bin/bash

# Reset Database Script
# This script stops the PostgreSQL container, removes all data, and starts fresh

echo "🔄 Resetting PostgreSQL database..."

# Stop and remove containers with volumes
echo "📦 Stopping containers and removing volumes..."
docker-compose down -v

# Start PostgreSQL fresh
echo "🚀 Starting PostgreSQL with fresh data..."
docker-compose up -d postgres

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
until docker-compose exec -T postgres pg_isready -U postgres; do
    echo "Waiting for PostgreSQL to start..."
    sleep 2
done

echo "✅ Database reset complete!"
echo "📊 PostgreSQL is running on localhost:5432"
echo "🗄️  Database: insurance_service"
echo "👤 User: insurance_app"
echo "🔑 Password: insurance_app_password" 