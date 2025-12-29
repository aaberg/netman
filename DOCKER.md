# Docker Setup Guide

This guide explains how to build and run the NetMan application using Docker and Docker Compose.

## Quick Start with Docker Compose

To start all services (database, API, web, etc.) with Docker Compose:

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

## Service Port Mappings

The following ports are mapped to avoid conflicts with local development:

| Service | Container Port | Host Port | Purpose |
|---------|---------------|-----------|---------|
| API | 8080 | 8081 | REST API (Docker) |
| Web | 3000 | 3000 | Web UI (Docker) |
| Database | 5432 | 5433 | PostgreSQL |
| Hanko | 8000 | 8000 | Authentication API |
| Hanko Admin | 8001 | 8001 | Authentication Admin |
| Mailslurper | 8080 | 8090 | Email testing UI |
| NATS | 4222 | 4222 | Messaging |

## Local Development vs Docker

### Running API Locally

When running the API locally (not in Docker), it will use port **8080** on localhost:

```bash
cd netman-api
./gradlew run
# API available at http://localhost:8080
```

### Running Web Locally

When running the web frontend locally (not in Docker), it will use port **5173**:

```bash
cd netman-web
npm install
npm run dev
# Web available at http://localhost:5173
```

When running locally, the frontend is configured to connect to the **dockerized API** at `http://localhost:8081` by setting the `NETMAN_API_URL` environment variable.

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
