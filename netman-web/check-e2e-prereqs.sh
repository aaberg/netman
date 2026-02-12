#!/bin/bash

# Quick Start Script for E2E Tests
# This script helps ensure all prerequisites are met before running tests

set -e

echo "=================================================="
echo "NetMan E2E Test Prerequisites Checker"
echo "=================================================="
echo ""

# Check if we're in the netman-web directory
if [ ! -f "package.json" ]; then
    echo "❌ Error: This script must be run from the netman-web directory"
    exit 1
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running"
    echo "   Please start Docker Desktop or Docker daemon"
    exit 1
fi
echo "✓ Docker is running"

# Check if we're in the right directory relative to docker-compose.yml
if [ ! -f "../docker-compose.yml" ] && [ ! -f "../compose.yml" ]; then
    echo "⚠️  Warning: Cannot find docker-compose.yml in parent directory"
    echo "   Make sure you're running from netman/netman-web/"
else
    echo "✓ Found Docker Compose configuration"
fi

# Check Hanko service
echo ""
echo "Checking Hanko service (http://localhost:8000)..."
if curl -s -f -m 3 http://localhost:8000/.well-known/jwks.json > /dev/null 2>&1; then
    echo "✓ Hanko service is accessible"
else
    echo "❌ Hanko service is not accessible at http://localhost:8000"
    echo ""
    echo "To start services, run from the repository root:"
    echo "  cd .."
    echo "  docker compose up -d --build"
    exit 1
fi

# Check API service
echo "Checking API service (http://localhost:8081)..."
if curl -s -f -m 3 http://localhost:8081/health > /dev/null 2>&1; then
    echo "✓ API service is accessible"
else
    echo "❌ API service is not accessible at http://localhost:8081"
    echo ""
    echo "To start services, run from the repository root:"
    echo "  cd .."
    echo "  docker compose up -d --build"
    exit 1
fi

echo ""
echo "=================================================="
echo "✅ All prerequisites are met!"
echo "=================================================="
echo ""
echo "You can now run E2E tests with:"
echo "  npm run test:e2e"
echo ""
