#!/bin/bash

# Script to set up environment variables

set -e

echo "🔧 Setting up environment variables..."

# Check if .env file already exists
if [ -f ".env" ]; then
    echo "⚠️  .env file already exists!"
    read -p "Do you want to overwrite it? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Aborted. Your existing .env file was not modified."
        exit 1
    fi
fi

# Copy example file
cp .env.example .env

echo "✅ .env file created from .env.example"
echo ""
echo "📝 Please edit the .env file with your actual values:"
echo "   - Database credentials"
echo "   - Auth0 configuration"
echo "   - Other sensitive data"
echo ""
echo "🔒 The .env file is already in .gitignore and will not be committed."
echo ""
echo "To load environment variables, you can:"
echo "1. Use Docker Compose: docker-compose --env-file .env up"
echo "2. Export them manually: source .env"
echo "3. Use a tool like dotenv for your IDE" 