#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Check if .env file exists
if [ ! -f .env ]; then
    echo "🚨 .env file not found. Please create one from .env.example."
    exit 1
fi

echo "🔄 Building and starting Freedom server in production mode..."

# Build and start the container using the production docker-compose file
docker-compose -f docker-compose.prod.yml up --build
