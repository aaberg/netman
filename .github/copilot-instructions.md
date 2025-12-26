# NetMan Copilot Instructions

This document provides guidance for GitHub Copilot coding agent when working on the NetMan repository.

## Repository Overview

NetMan is a monorepo consisting of:
- **netman-api**: Kotlin/Micronaut REST API backend
- **netman-web**: Svelte/SvelteKit web application frontend

Both components use Docker Compose for orchestration with PostgreSQL databases and authentication services.

## Architecture Principles

- Follow **iDesign method** for architecture definition
- Use **volatility-based decomposition** when designing components/services
- Adhere to **SOLID principles** in all code
- Prefer composition over inheritance

## Development Workflow

### Setting Up the Environment

All services are orchestrated via Docker Compose:
```bash
# Start all services
docker compose up -d --build

# View logs
docker compose logs -f

# Stop services
docker compose down
```

### Running Components Individually

**Backend (netman-api):**
```bash
cd netman-api
./gradlew clean build
./gradlew test
```

**Frontend (netman-web):**
```bash
cd netman-web
npm install
npm run dev
npm run test        # Unit tests (Vitest)
npm run test:e2e    # E2E tests (Playwright)
```

## Code Style and Standards

### General Guidelines
- Write clear, self-documenting code
- Add comments only when they add value beyond what the code expresses
- Use existing patterns and libraries when possible
- Follow industry best practices

### Backend (Kotlin/Micronaut)
- Use Kotlin idiomatic code style
- Follow Kotlin naming conventions (camelCase for functions/variables, PascalCase for classes)
- Leverage Kotlin language features (data classes, extension functions, null safety)
- Use Micronaut annotations properly for dependency injection
- Follow REST API best practices
- Maintain at least **80% code coverage** for tests

### Frontend (Svelte/TypeScript)
- Use TypeScript for type safety
- Follow Prettier configuration (see `netman-web/.prettierrc`)
- Use ESLint configuration (see `netman-web/eslint.config.js`)
- Format code with: `npm run format`
- Lint code with: `npm run lint`
- Component naming: PascalCase for Svelte components
- Use Tailwind CSS (with DaisyUI) for styling
- Prefer Svelte 5 features and runes

### Prettier Configuration
- Use spaces (not tabs)
- Tab width: 2
- No semicolons
- Print width: 100
- No trailing commas

## Testing

### Backend Testing
- Use JUnit 5 for all tests
- Use WireMock for API mocking in integration tests
- Use RestAssured for REST API testing
- Test class naming: `*Test.kt`
- Follow Arrange-Act-Assert pattern
- Maintain minimum 80% code coverage

Example test structure:
```kotlin
@WireMockTest(httpPort = 8091)
@MicronautTest
class ExampleApiTest {
    @Test
    fun `test description in backticks`(spec: RequestSpecification) {
        // Arrange
        // ... setup
        
        // Act & Assert
        spec.when()
            .get("/api/endpoint")
        .then()
            .statusCode(200)
    }
}
```

### Frontend Testing
- Use Vitest for unit/component tests
- Use Playwright for E2E tests
- Test files: `*.spec.ts` or `*.test.ts`
- Use Testing Library for component testing

## Database and Migrations

- PostgreSQL 17 is used for both main app and Hanko authentication
- Liquibase handles database migrations
- Migration files are in `/liquibase` directory
- Always create migrations for schema changes

## Security

- Use Hanko for authentication (see `hanko-config.yaml`)
- Never commit secrets or credentials
- Use environment variables for sensitive configuration
- Validate all user inputs
- Follow security best practices for REST APIs

## API Development

- OpenAPI/Swagger annotations are used for API documentation
- Use proper HTTP status codes
- Implement proper error handling
- Use Micronaut validation annotations
- Return consistent response structures

## Important Conventions

### File Organization
- Keep related functionality together
- Backend: Follows layered architecture (Api → Managers → Engines → Access)
  - `/api/` - Controllers and API interfaces organized by feature/domain (e.g., `/api/membership`, `/api/tenant`)
  - `/businesslogic/` - Managers and Engines layers
  - `/access/` - Access Layer with Repository classes and API clients
- Frontend: Components in `/components`, lib code in `/lib`, routes in `/routes`

### Environment Variables
- Frontend: Prefix public env vars with `PUBLIC_` (e.g., `PUBLIC_API_BASE_URL`)
- Backend: Use Micronaut's configuration system

### Ports
- Web UI: 5173 (development)
- API: 8080
- Main DB: 5433 (host) → 5432 (container)
- Hanko DB: 5432 (host) → 5432 (container)
- Mailslurper: 8090

## Build Artifacts and Dependencies

Do not commit:
- `node_modules/`
- `build/`
- `dist/`
- `.gradle/`
- `*.class`
- IDE-specific files (except committed `.idea` or `.fleet` configurations)

## Linting and Formatting

Before committing:
- **Backend**: Run `./gradlew check`
- **Frontend**: Run `npm run lint` and `npm run format`

## Common Tasks

### Adding a New API Endpoint
1. Define interface in appropriate API file (e.g., `TenantApi.kt`)
2. Implement controller (e.g., `TenantApiController.kt`)
3. Add OpenAPI annotations
4. Write integration tests
5. Update OpenAPI documentation if needed

### Adding a New UI Component
1. Create component in `/components` directory
2. Use TypeScript for props and types
3. Follow existing component patterns
4. Add unit tests if component has logic
5. Use Tailwind CSS for styling

### Database Changes
1. Create new Liquibase changeset in `/liquibase`
2. Test migration with Docker Compose
3. Update relevant data access code
4. Add/update tests

## Do Not Modify

- Build configuration files without explicit request
- Docker Compose configuration unless necessary
- Authentication/security configuration without careful review
- Database migration files that have already been applied

## Resources

- Main README: `/README.md`
- Backend README: `/netman-api/README.md`
- Frontend README: `/netman-web/README.md`
- Backend Developer Agent: `.github/agents/backend-developer.agent.md`
