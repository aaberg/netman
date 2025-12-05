# NetMan — API (Micronaut/Kotlin) and Web (Svelte) Monorepo

NetMan is composed of two primary components:
- netman-api — Kotlin Micronaut REST API
- netman-web — Svelte/SvelteKit web application

A Docker Compose setup orchestrates the full development and test environment (e.g., backing services and wiring between API and Web). Use Compose both for local development and to run tests consistently.

This README provides an overview, prerequisites, and step-by-step guides to run the project with and without Docker Compose, plus tips for configuration and testing.

---

## Repository Structure

At the top-level project root, you should find:
- /netman-api — Micronaut API service
- /netman-web — Svelte web application
- docker-compose.yml (or compose.yaml) — Services for local development and tests

## Prerequisites

- Docker Desktop 4.x or later (Docker Engine + Docker Compose v2)
- Node.js 20+ and npm 10+ (for working on the web app locally)
- Java 25+ (for working on the API locally)
- Git

## Quick Start (Recommended): Run Everything via Docker Compose

From the top-level project folder containing docker-compose.yml:

```powershell
# Start all services in the background
docker compose up -d --build

# View logs (press Ctrl+C to stop tailing)
docker compose logs -f

# Stop services
docker compose down
```

Service URLS:
- Web UI: http://localhost:5173
- API: http://localhost:8090
- Mailslurper: http://localhost:8080

Environment-specific values (ports, base URLs, credentials) are controlled via the compose file and .env files. Check docker-compose.yml (and any referenced .env) for the actual values.

## Running Components Individually (Without Docker)

You can run the API and Web independently for local development.

### 1) API — Micronaut/Kotlin (netman-api)

From the netman-api folder:

```powershell
# Windows
./gradlew.bat clean run

# Alternatively, to run tests
./gradlew.bat test
```

### 2) Web — Svelte/SvelteKit (netman-web)

From the netman-web folder:

```powershell
npm install
npm run dev
# Or open a browser automatically
npm run dev -- --open
```

The dev server typically runs on http://localhost:5173.

To create a production build:

```powershell
npm run build
# Preview the prod build locally
npm run preview
```

## Configuration

- API base URL for the web app:
  - In SvelteKit, expose client-side env vars by prefixing with PUBLIC_. For example: PUBLIC_API_BASE_URL=http://localhost:8080
  - You can define env vars in a .env file at the project root or in the netman-web folder depending on your setup.
- CORS: Ensure the API allows the web origin when running separately (e.g., localhost:5173). Check Micronaut CORS configuration.
- Ports: If you change ports in Docker Compose, update the web app’s API base URL accordingly.

## Testing

### Backend (Micronaut)

From netman-api:
```powershell
./gradlew.bat test
```

### Frontend (Svelte)

From netman-web:
```powershell
# Unit/component tests
npm run test

# End-to-end tests (Playwright)
# Ensure the app (and API if needed) is running; Docker Compose is recommended
npm run test:e2e
```

This repository includes Playwright and Vitest configuration (see playwright.config.ts and vitest-setup-client.ts). The e2e tests may expect services from Docker Compose; run docker compose up beforehand for a stable environment.

## Developing with Docker + Hot Reload

A common workflow is:
- Use Docker Compose to run backing services (databases, queues, etc.) and optionally the API.
- Run the web app locally with npm run dev for fast hot module replacement.
- Alternatively, run both API and Web with hot reload locally, and use Compose only for dependencies.

Check your compose file for bind mounts that enable live reload inside containers. If not present, prefer running the apps locally as above.

## Troubleshooting

- Ports already in use: Stop conflicting processes or change ports in docker-compose.yml and/or app configs.
- API not reachable from Web: Verify PUBLIC_API_BASE_URL, CORS settings on the API, and container networking when using Compose.
- Windows path issues: Prefer PowerShell commands shown here. If using Git Bash or WSL, adjust paths accordingly.
- Dependency install problems: Ensure you’re on Node 20+ and have a clean node_modules (delete and reinstall if needed).

## Contributing

- Use conventional commits if possible (feat, fix, docs, chore, etc.).
- Create feature branches from main.
- Add/adjust tests where appropriate.
- Run linters/formatters before committing.

## License

Add your project’s license information here (e.g., MIT, Apache-2.0).

## Continuous Integration

This repository uses GitHub Actions for CI/CD. The CI workflow automatically runs on:
- Push to `main` branch
- Pull requests to `main` branch

### CI Workflow

The CI workflow (`.github/workflows/ci.yml`) includes two parallel jobs:

**Backend Job:**
- Sets up JDK 17 and PostgreSQL 17
- Builds the Kotlin/Micronaut API
- Runs all backend tests

**Frontend Job:**
- Sets up Node.js 20
- Installs dependencies
- Lints and checks code formatting
- Builds the Svelte/SvelteKit application
- Runs unit tests

Both jobs must pass for the CI check to succeed.
