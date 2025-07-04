#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

echo "🔄 Building and starting Freedom server..."

# Build and start the container
docker-compose up --build
