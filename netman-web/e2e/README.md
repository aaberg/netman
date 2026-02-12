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

The tests require the full application stack to be running:
- PostgreSQL database
- Hanko authentication service
- NetMan API backend
- NetMan web frontend

The easiest way to set this up is using Docker Compose:

```bash
# From the repository root
docker compose up -d --build
```

Wait for all services to be healthy before running tests.

### Running Tests

```bash
# From the netman-web directory
npm run test:e2e
```

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
