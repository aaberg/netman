# NetMan Engineering Guidelines

This document captures the coding conventions, code organization, and testing approaches used across the two main projects in this repository:

- netman-api (Kotlin + Micronaut)
- netman-web (Svelte/SvelteKit + TypeScript)

Use these guidelines for new development and when refactoring existing code.

---

## 1) Coding conventions

### 1.1 netman-api (Kotlin + Micronaut)
- Language and framework
  - Kotlin 2.x, Micronaut 4.x (Netty runtime), KSP for annotation processing.
  - Reactive support available via micronaut-reactor when needed.
- Kotlin style
  - Follow standard Kotlin coding conventions (official JetBrains/Kotlin style).
  - Prefer immutable data (val over var) and data classes for simple DTOs and value objects.
  - Use expressive, backticked test names for readability (e.g., `fun `create a new contact``).
  - Use PascalCase for classes/types, camelCase for functions/variables, UPPER_SNAKE_CASE for constants.
- Dependency injection and configuration
  - Constructor injection by default. Avoid field injection except where a framework requires it.
  - Scope beans with @Singleton unless a narrower scope is required.
  - Keep configuration in application.yml/properties; override in application-test.properties for tests.
- API modeling
  - Request/response models should be explicit DTOs (do not expose persistence entities).
  - Validate input using Micronaut validation annotations; prefer value classes and enums for constrained types.
- HTTP clients and external calls
  - Use Micronaut HttpClient for outbound calls. In tests, mock/externalize via WireMock.
- Logging and errors
  - Use SLF4J via Logback. Prefer structured log messages and avoid logging sensitive data.
  - Map domain/service errors to proper HTTP statuses via exception handlers.

### 1.2 netman-web (Svelte/SvelteKit + TypeScript)
- Language and tooling
  - TypeScript with strict mode enabled (see tsconfig.json: strict: true).
  - Lint with ESLint 9 and format with Prettier 3 (+ svelte and tailwind plugins).
- Svelte conventions
  - One component per file; component filenames in PascalCase.svelte.
  - Co-locate small helpers with components; shared code under src/lib.
  - Prefer store subscriptions via $store syntax inside Svelte and derived stores for computed state.
- Styling
  - Tailwind CSS 4 + DaisyUI for UI primitives. Use utility-first classes; avoid ad-hoc global CSS.
  - Class ordering is managed by prettier-plugin-tailwindcss; don’t hand-reorder utility classes.
- Accessibility and UX
  - Use semantic HTML, label interactive elements, and prefer keyboard-accessible controls.
  - Add aria-* attributes and roles where appropriate.

---

## 2) Code organization and package structure

### 2.1 netman-api
- High-level package layout (observed examples in current codebase):
  - netman.api.v1.<domain> … Public HTTP layer (controllers + request/response DTOs) for versioned API endpoints. Example: netman.api.v1.membership.
  - netman.api.contact … Additional API surface for contact-related endpoints (some legacy/unversioned paths exist; prefer v1 for new code).
  - netman.businesslogic … Core application/domain services. Keep orchestration and business rules here.
  - netman.access.repository … Data access, repositories, and test property helpers.
  - netman.api.access.client … HTTP/SDK clients to external services (e.g., AuthenticationClient).
- Controllers
  - Keep controllers thin: validate/deserialize, delegate to businesslogic, map results to DTOs/HTTP.
  - Group controllers and DTOs by bounded context (membership, contact, etc.).
  - Prefer versioned routes under /api/v1/... for new endpoints.
- Configuration and resources
  - Resources live under src/main/resources; test overrides under src/test/resources (e.g., application-test.properties).
- Build/dependencies
  - Managed via Gradle Kotlin DSL (build.gradle.kts). Application entry: netman.ApplicationKt.

### 2.2 netman-web
- Project layout (SvelteKit defaults)
  - src/routes … Route-driven pages and endpoints (+page.svelte, +page.ts, +page.server.ts, +layout.svelte, etc.).
  - src/lib … Shared UI components, stores, utilities, types.
  - static … Public static assets.
  - e2e … Playwright tests.
- Routing and data loading
  - Use +page.ts/+layout.ts load functions for client-side data needs; use +page.server.ts for server-side-only concerns.
  - Keep API base URL configurable via PUBLIC_… env params (e.g., PUBLIC_API_BASE_URL) and avoid hardcoding endpoints.
- State management
  - Prefer simple Svelte stores; avoid heavy global state unless necessary.

---

## 3) Unit and integration test approaches

### 3.1 netman-api
- Frameworks and libraries
  - JUnit 5 (jupiter), AssertJ, Micronaut Test (micronaut-test-junit5), RestAssured (micronaut-test-rest-assured).
  - WireMock (JUnit 5 extension) for mocking external HTTP dependencies.
  - Optional Testcontainers (currently commented out in build) can be enabled for DB integration tests when needed.
- Unit tests
  - Scope: pure functions and service logic. Mock collaborators; avoid the full Micronaut context.
  - Assertions: use AssertJ or JUnit assertions. Keep tests deterministic and fast.
- Integration/API tests
  - Use @MicronautTest(transactional = false) to boot the application context.
  - Inject RequestSpecification for RestAssured-based HTTP tests.
  - Use @WireMockTest to stand up mock servers for outbound calls (e.g., auth). Keep fixed ports and per-test stubbing for clarity.
  - Configure via src/test/resources/application-test.properties to isolate test settings.
  - Test naming: use descriptive backticked names and Given/When/Then structure in comments where helpful.
- Data and state
  - Prefer test-local data setup through business services (e.g., membershipManager.registerUserWithPrivateTenant(...)).
  - If/when enabling Testcontainers, use a per-testcontainer or per-suite container pattern and clear data between tests.

### 3.2 netman-web
- Unit and component tests (Vitest + @testing-library/svelte)
  - Render components with realistic props; assert against accessible queries (getByRole, getByLabelText) over test IDs when possible.
  - Use jsdom environment; stub network with fetch mocks when components call the API.
  - Keep tests colocated under src with .test.ts/.spec.ts or use a tests/ folder per component directory.
- End-to-end tests (Playwright)
  - Test directory is e2e; configuration defined in playwright.config.ts (build + preview on port 4173).
  - Prefer user-centric flows: navigation, forms, error handling, and API integration via the running preview server.
  - Use data-testid selectively for non-accessible elements; otherwise prefer role- and label-based selectors.

---

## 4) Commands and quality gates

### 4.1 netman-api
- Build and test
  - From netman-api directory: ./gradlew test (Windows: .\gradlew test)
  - Run application: .\gradlew run or build a shadow jar.
- Lint/format
  - Follow Kotlin conventions; if ktlint/detekt are added in future, integrate into the Gradle lifecycle.

### 4.2 netman-web
- Install and run
  - npm install
  - npm run dev (local dev server)
  - npm run build, npm run preview
- Quality and tests
  - npm run lint (Prettier check + ESLint)
  - npm run format (Prettier write)
  - npm run test:unit (Vitest)
  - npm run test:e2e (Playwright)
  - npm run test (runs unit then e2e)

---

## 5) General guidance
- Keep layers separated: controllers → services → repositories/clients.
- Prefer small, cohesive modules and functions.
- Write tests along with features; aim for fast unit tests and targeted, reliable integration tests.
- Document public endpoints and DTOs; prefer OpenAPI annotations for discoverability.
- Avoid leaking infrastructure concerns (DB, wire protocols) into domain logic.
