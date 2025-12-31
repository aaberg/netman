# Docker Setup Guide

This guide explains how to build and run the NetMan application using Docker and Docker Compose.

## Compose File Overview

NetMan provides two Docker Compose files for different use cases:

- **`compose.yml`**: Full application stack (dependencies + API + Web)
- **`compose.deps.yml`**: Dependencies only (database, auth, messaging, etc.)

The `compose.yml` file includes `compose.deps.yml` using Docker Compose's inheritance feature, so you only need to maintain the dependencies in one place.

## Quick Start with Docker Compose

### Option 1: Full Stack (All Services)

To start all services including API and web:

```bash
# Build and start all services
docker compose up -d --build

# View logs
docker compose logs -f

# View logs for specific service
docker compose logs -f api
docker compose logs -f web

# Stop all services
docker compose down

# Stop and remove volumes
docker compose down -v
```

### Option 2: Dependencies Only (For Local Development)

To start only the dependencies and run API/web locally:

```bash
# Start only dependencies
docker compose -f compose.deps.yml up -d

# View logs
docker compose -f compose.deps.yml logs -f

# Stop dependencies
docker compose -f compose.deps.yml down
```

This is useful when:
- Developing the API or web application locally
- You want faster iteration cycles without rebuilding Docker images
- You need to debug the application with your IDE

After starting dependencies, you can run the API and/or web locally:

```bash
# Run API locally
cd netman-api
./gradlew run

# Run web locally (in another terminal)
cd netman-web
npm run dev
```

## Service Port Mappings

The following ports are mapped to avoid conflicts with local development:

| Service | Container Port | Host Port | Purpose | Compose File |
|---------|---------------|-----------|---------|--------------|
| API | 8080 | 8081 | REST API (Docker) | compose.yml |
| Web | 3000 | 3000 | Web UI (Docker) | compose.yml |
| Database | 5432 | 5433 | PostgreSQL | compose.deps.yml |
| Hanko | 8000 | 8000 | Authentication API | compose.deps.yml |
| Hanko Admin | 8001 | 8001 | Authentication Admin | compose.deps.yml |
| Mailslurper | 8080 | 8090 | Email testing UI | compose.deps.yml |
| NATS | 4222 | 4222 | Messaging | compose.deps.yml |

## Dependencies Included in compose.deps.yml

The dependencies-only compose file includes:
- **db**: PostgreSQL 17 database for the application
- **liquibase**: Database migration tool
- **postgres_hanko**: PostgreSQL database for Hanko authentication
- **hanko-migrate**: Hanko database migration
- **hanko**: Hanko authentication service
- **mailslurper**: Email testing service
- **nats**: NATS messaging server

## Local Development vs Docker

### Running API Locally with Dependencies in Docker

The most common development workflow is to run dependencies in Docker and the API locally:

```bash
# 1. Start dependencies
docker compose -f compose.deps.yml up -d

# 2. Wait for services to be healthy
docker compose -f compose.deps.yml ps

# 3. Run API locally
cd netman-api
./gradlew run
# API available at http://localhost:8080
```

### Running Web Locally with Dependencies in Docker

Similarly, for web development:

```bash
# 1. Start dependencies (if not already running)
docker compose -f compose.deps.yml up -d

# 2. Run web locally
cd netman-web
npm install
npm run dev
# Web available at http://localhost:5173
```

### Running Full Stack in Docker

For testing the complete dockerized application:

```bash
docker compose up -d --build
# API available at http://localhost:8081 (note different port!)
# Web available at http://localhost:3000
```

### Mixed Mode (Frontend Local + API Docker)

You can run the frontend locally while using the dockerized API:

1. Start Docker services (including API):
   ```bash
   docker compose up -d
   ```

2. Run frontend locally:
   ```bash
   cd netman-web
   export NETMAN_API_URL=http://localhost:8081
   npm run dev
   ```

The frontend will be available at `http://localhost:5173` and will connect to the dockerized API at `http://localhost:8081`.

## Building Individual Services

### API Docker Image

**Development Build (JVM - Fast):**
```bash
cd netman-api
docker build -t netman-api:dev .
```

**Production Build (Native - Slow but optimal):**
```bash
cd netman-api
docker build -f Dockerfile.native -t netman-api:prod .
```

Or using Gradle's dockerBuildNative task:
```bash
cd netman-api
./gradlew dockerBuildNative
```

### Web Docker Image

```bash
cd netman-web
docker build -t netman-web:latest .
```

## Docker Image Types

### API Dockerfiles

- **`Dockerfile`**: JVM-based build, faster build times (~2-5 minutes), good for development
- **`Dockerfile.native`**: GraalVM native image, slower build (~10-15 minutes), optimal runtime performance for production

The docker-compose.yml uses the standard `Dockerfile` (JVM) for faster iteration during development.

For production deployments, GitHub Actions uses `Dockerfile.native` to build optimized native images.

## GitHub Container Registry

Docker images are automatically published to GitHub Container Registry (ghcr.io) when a new release is created:

```bash
# Pull published images
docker pull ghcr.io/aaberg/netman/api:latest
docker pull ghcr.io/aaberg/netman/web:latest
docker pull ghcr.io/aaberg/netman/migrate:latest

# Or specific version
docker pull ghcr.io/aaberg/netman/api:1.0.0
docker pull ghcr.io/aaberg/netman/web:1.0.0
docker pull ghcr.io/aaberg/netman/migrate:1.0.0
```

## Environment Variables

### API Environment Variables

- `NETMAN_PORT`: Port to run the API on (default: 8080)
- `NETMAN_JDBC_URL`: PostgreSQL connection URL
- `NETMAN_JDBC_USER`: Database username
- `NETMAN_JDBC_PASSWORD`: Database password
- `NETMAN_NATS_URL`: NATS server URL

### Web Environment Variables

- `NETMAN_API_URL`: Backend API URL (server-side)
- `PUBLIC_HANKO_API_URL`: Hanko authentication URL (client-side)
- `PORT`: Port to run the web server on (default: 3000)
- `HOST`: Host to bind to (default: 0.0.0.0)
- `NODE_ENV`: Node environment (production/development)

## Troubleshooting

### Port Already in Use

If you see "port already in use" errors:

1. Check what's using the port:
   ```bash
   lsof -i :8081  # For API
   lsof -i :3000  # For web
   ```

2. Stop the conflicting process or change the port mapping in `compose.yml`

### Build Failures

If builds fail:

1. **For gradlew not found errors**, rebuild without cache to ensure the latest Dockerfile changes are used:
   ```bash
   docker compose build --no-cache
   ```

2. Clean Docker build cache:
   ```bash
   docker builder prune
   ```

3. If you see "gradlew: not found" even after rebuilding, the cached layers may be stale. Force a complete rebuild:
   ```bash
   docker compose down
   docker compose build --no-cache --pull
   docker compose up -d
   ```

### Cannot Connect to API

If the web frontend cannot connect to the API:

1. Check API is healthy:
   ```bash
   docker compose ps
   curl http://localhost:8081/health
   ```

2. Check logs for errors:
   ```bash
   docker compose logs api
   ```

### Native Build Taking Too Long

Native builds with GraalVM can take 10-15 minutes. For development:

- Use the standard `Dockerfile` (JVM-based)
- Only use `Dockerfile.native` for production builds

## CI/CD

The GitHub Actions workflow (`.github/workflows/docker-publish.yml`) automatically:

1. Builds both API and Web Docker images
2. Publishes them to GitHub Container Registry
3. Tags them with the release version
4. Generates build attestations for supply chain security

This workflow triggers on:
- New GitHub releases (uses release tag as version)
- Manual workflow dispatch (specify version manually)
