# End-to-End Tests

This directory contains Playwright end-to-end tests for the NetMan web application.

## Test Coverage

The test suite covers the following key functionality:

### Authentication Tests (`auth.test.ts`)
- User registration with new account
- User login with existing account
- Session management

### Contact Tests (`contacts.test.ts`)
- Creating a contact with basic information
- Creating a contact with multiple details (email, phone, notes)
- Viewing contact details
- Contact list display

### Follow-up Tests (`followups.test.ts`)
- Creating follow-up tasks with relative time (e.g., "in 7 days")
- Creating follow-up tasks with absolute time (specific date/time)
- Viewing follow-up tasks list
- Form validation for follow-up creation

### Workflow Tests (`workflow.test.ts`)
- Complete end-to-end user journey (register → create contact → add follow-up → view dashboard)
- Navigation between different sections of the app
- Multiple contacts and follow-ups workflow

## Running the Tests

### Prerequisites

⚠️ **IMPORTANT**: The tests require the full application stack to be running:
- PostgreSQL database
- Hanko authentication service (port 8000)
- NetMan API backend (port 8081)
- NetMan web frontend (automatically started by Playwright)

**The tests WILL HANG or FAIL if these services are not running!**

#### Starting the Required Services

The easiest way to set this up is using Docker Compose:

```bash
# From the repository root (not from netman-web directory)
cd /path/to/netman
docker compose up -d --build
```

Wait for all services to be healthy before running tests:

```bash
# Check service status
docker compose ps

# All services should show "healthy" or "running"
# Example output:
# NAME                COMMAND                  SERVICE    STATUS      PORTS
# netman-api-1       "java -jar app.jar"       api        Up (healthy) 0.0.0.0:8081->8080/tcp
# netman-hanko-1     "./hanko serve"           hanko      Up          0.0.0.0:8000->8000/tcp
# netman-db-1        "docker-entrypoint..."    db         Up (healthy) 0.0.0.0:5433->5432/tcp
```

#### Verifying Services Are Ready

Before running tests, verify services are accessible:

```bash
# Check Hanko service
curl http://localhost:8000/.well-known/jwks.json

# Check API service
curl http://localhost:8081/health
```

Both should return valid responses (not connection errors).

#### Quick Prerequisites Check

You can also use the provided script to check if all prerequisites are met:

```bash
# From the netman-web directory
./check-e2e-prereqs.sh
```

This script will verify that:
- Docker is running
- Hanko service is accessible
- API service is accessible

### Running Tests

```bash
# From the netman-web directory
npm run test:e2e
```

The global setup will automatically verify services are running before tests start.
If services are not available, you'll see a clear error message with instructions.

This will:
1. Build the application
2. Start a preview server on port 4173
3. Run all tests in the `e2e` directory
4. Generate an HTML report

### Running Specific Tests

```bash
# Run only authentication tests
npx playwright test auth.test.ts

# Run only contact tests
npx playwright test contacts.test.ts

# Run only follow-up tests
npx playwright test followups.test.ts

# Run only workflow tests
npx playwright test workflow.test.ts
```

### Debug Mode

To run tests in debug mode with the Playwright Inspector:

```bash
npx playwright test --debug
```

### View Test Report

After running tests, view the HTML report:

```bash
npx playwright show-report
```

## Test Utilities

### Test Helpers (`utils/test-helpers.ts`)

The test helpers module provides utility functions for:
- Generating unique test data (emails, names, contact names)
- Interacting with Hanko authentication (Shadow DOM handling)
- Navigation helpers
- URL parsing utilities

## Important Notes

### Hanko Authentication

The application uses Hanko Elements for authentication, which uses Shadow DOM. Our test helpers include special functions to interact with Shadow DOM elements:
- `waitForHankoAuth()` - Wait for Hanko component to load
- `fillHankoEmail()` - Fill email field in Hanko Shadow DOM
- `fillHankoPassword()` - Fill password field in Hanko Shadow DOM
- `clickHankoButton()` - Click buttons in Hanko Shadow DOM

### Test Data

Each test generates unique data (emails, names, etc.) to avoid conflicts. The test suite is designed to run in isolation without requiring database cleanup between tests.

### Test Configuration

The Playwright configuration is in `playwright.config.ts` at the root of the `netman-web` directory. Key settings:
- Tests run sequentially (not in parallel) to ensure stability
- Base URL: http://localhost:4173
- Screenshots are captured on failure
- Traces are captured on first retry

## Troubleshooting

### Tests Hang or Don't Start

**Problem**: Tests appear to hang indefinitely without starting or showing progress.

**Cause**: The required services (Hanko auth and/or NetMan API) are not running.

**Solution**:
1. Make sure Docker Compose is running:
   ```bash
   cd /path/to/netman  # Repository root
   docker compose up -d --build
   ```

2. Verify all services are healthy:
   ```bash
   docker compose ps
   # Look for "healthy" status on all services
   ```

3. Check service accessibility:
   ```bash
   # Hanko should respond
   curl http://localhost:8000/.well-known/jwks.json
   
   # API should respond
   curl http://localhost:8081/health
   ```

4. If services are not responding, check logs:
   ```bash
   docker compose logs hanko
   docker compose logs api
   ```

5. If services are failing to start, try a clean restart:
   ```bash
   docker compose down -v
   docker compose up -d --build
   ```

### Tests Timing Out

If tests are timing out:
1. Ensure all Docker services are running and healthy
2. Check that the preview server started successfully
3. Verify Hanko service is accessible at http://localhost:8000
4. Verify API service is accessible at http://localhost:8081

### Shadow DOM Errors

If you encounter errors about Shadow DOM not being found:
1. Ensure Hanko Elements is properly loaded
2. Check that the `PUBLIC_HANKO_API_URL` environment variable is set
3. Try increasing wait times in test helpers

### Database Issues

If tests fail due to database errors:
1. Check Docker logs: `docker compose logs db`
2. Verify database migrations ran: `docker compose logs liquibase`
3. Restart the services: `docker compose down && docker compose up -d`

## Adding New Tests

When adding new tests:
1. Use the existing test helpers for common operations
2. Follow the established pattern of test structure
3. Generate unique test data for each test
4. Clean up after tests if necessary
5. Add appropriate assertions to verify behavior
6. Document any new test utilities in this README
