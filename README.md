# NetMan

NetMan is a modern contact management and networking application built with Micronaut and SvelteKit.

## Architecture

- **Backend**: Kotlin/Micronaut REST API
- **Frontend**: SvelteKit web application
- **Database**: PostgreSQL 17
- **Authentication**: Hanko
- **Messaging**: NATS

## Quick Start

### Using Docker Compose (Recommended)

The fastest way to get started is using Docker Compose which runs all services:

```bash
# Start all services (dependencies + API + web)
docker compose up -d --build

# View logs
docker compose logs -f

# Access the application
# - Web UI: http://localhost:3000
# - API: http://localhost:8081
# - API Documentation: http://localhost:8081/swagger-ui
```

For detailed Docker instructions, see [DOCKER.md](DOCKER.md).

### Local Development

For local development, you can run just the dependencies in Docker and run the API/web locally:

#### Prerequisites

- Java 25
- Node.js 20+
- Docker and Docker Compose (for supporting services)

#### Start Dependencies Only

```bash
# Start only the supporting services (database, auth, messaging, etc.)
docker compose -f compose.deps.yml up -d

# View logs
docker compose -f compose.deps.yml logs -f
```

#### Backend (API)

```bash
# Dependencies should be running (see above)

# Run the API locally
cd netman-api
./gradlew run

# API available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui
```

#### Frontend (Web)

```bash
# Copy environment template
cd netman-web
cp .env.example .env

# Install dependencies
npm install

# Run development server
npm run dev

# Web available at http://localhost:5173
```

## Development

### Backend Development

```bash
cd netman-api

# Build
./gradlew build

# Run tests
./gradlew test

# Check code coverage
./gradlew test jacocoTestReport
```

### Frontend Development

```bash
cd netman-web

# Install dependencies
npm install

# Run dev server
npm run dev

# Run unit tests
npm run test:unit

# Run E2E tests
npm run test:e2e

# Lint code
npm run lint

# Format code
npm run format
```

## NATS Messaging

The application uses NATS for pub/sub messaging. The NATS server is included in the Docker Compose setup.

### Task Trigger Processing

The application subscribes to the `task.trigger.due` subject to process due task triggers. When a message is published to this subject, the application will:
1. Find all pending triggers whose trigger time has passed
2. Mark the corresponding tasks as "Due"
3. Mark the triggers as "Triggered"

### Using NATS CLI

To manually trigger task processing, you can publish a message using the NATS CLI:

#### Install NATS CLI

```bash
# macOS
brew install nats-io/nats-tools/nats

# Windows (using Scoop)
scoop install nats

# Linux
curl -sf https://binaries.nats.dev/nats-io/natscli/nats@latest | sh

# Or download from: https://github.com/nats-io/natscli/releases
```

#### Publish a Message

```bash
# Publish to the task.trigger.due subject (no payload needed)
nats pub task.trigger.due ""

# Or with confirmation
nats pub task.trigger.due "" --count 1
```

The subscriber uses a queue group (`task-trigger-processors`), which ensures that only one instance of the application will process each message when multiple instances are running. This enables horizontal scaling of the application.


## Contributing

- Use conventional commits if possible (feat, fix, docs, chore, etc.).
- Create feature branches from main.
- Add/adjust tests where appropriate.
- Run linters/formatters before committing.

## License

Add your project's license information here (e.g., MIT, Apache-2.0).
