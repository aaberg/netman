---
applies_to: [netman-api/**]
---

# Backend (Kotlin/Micronaut) Instructions

This file provides specific guidance for working on the Kotlin/Micronaut backend API.

## Technology Stack

- **Language**: Kotlin 2.2.21
- **Framework**: Micronaut 4.5.4
- **Database**: PostgreSQL 17 with Micronaut Data JDBC
- **Build Tool**: Gradle 8.x
- **Testing**: JUnit 5, WireMock, RestAssured
- **API Documentation**: OpenAPI/Swagger

## Code Organization

The backend follows a layered architecture:

```
netman-api/
├── src/
│   ├── main/kotlin/netman/
│   │   ├── api/           # Controllers and API interfaces
│   │   ├── access/        # Authentication and authorization
│   │   ├── models/        # Data models and DTOs
│   │   └── ...
│   └── test/kotlin/netman/
│       └── api/           # Integration tests
└── build.gradle.kts
```

## Development Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

# Run tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Check code quality
./gradlew check
```

## Kotlin Code Style

### Naming Conventions
- Classes: PascalCase (e.g., `TenantApiController`)
- Functions/Variables: camelCase (e.g., `getUserProfile`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- Private properties: camelCase with optional underscore prefix if needed

### Language Features to Prefer
- Use `data class` for DTOs and value objects
- Use `sealed class` for representing restricted hierarchies
- Leverage null safety (`?`, `!!`, `?.`, `?:`)
- Use extension functions to add functionality
- Prefer `when` expressions over `if-else` chains
- Use scope functions (`let`, `apply`, `run`, `also`, `with`) appropriately
- Use destructuring declarations where appropriate
- Prefer immutable collections (`listOf`, `setOf`, `mapOf`)

### Example Patterns

**Data Transfer Objects:**
```kotlin
data class TenantResource(
    val id: UUID,
    val name: String,
    val createdAt: Instant
)
```

**Controller Pattern:**
```kotlin
@Controller("/api/tenant")
class TenantApiController(
    private val tenantService: TenantService
) : TenantApi {
    
    @Get("/{id}")
    @ExecuteOn(TaskExecutors.IO)
    override fun getTenant(@PathVariable id: UUID): TenantResource {
        return tenantService.findById(id)
    }
}
```

## Micronaut Best Practices

### Dependency Injection
- Use constructor injection (preferred)
- Declare dependencies as `private val` in constructor
- Use appropriate scopes (@Singleton, @RequestScope, @Prototype)

### Annotations
- `@Controller`: For REST controllers
- `@Get`, `@Post`, `@Put`, `@Delete`: For HTTP methods
- `@PathVariable`, `@QueryValue`, `@Body`: For parameters
- `@ExecuteOn(TaskExecutors.IO)`: For blocking operations
- `@Validated`: For request validation
- Use OpenAPI annotations for documentation

### Error Handling
- Use `@Error` annotation for error handlers
- Return appropriate HTTP status codes
- Provide meaningful error messages
- Log errors appropriately

## Database Access

### Micronaut Data
- Use repository pattern
- Define interfaces extending `JdbcRepository`
- Use `@Query` for custom queries
- Prefer parameterized queries to prevent SQL injection

**Example Repository:**
```kotlin
@JdbcRepository(dialect = Dialect.POSTGRES)
interface TenantRepository : PageableRepository<Tenant, UUID> {
    
    @Query("SELECT * FROM tenant WHERE name = :name")
    fun findByName(name: String): Optional<Tenant>
}
```

## Testing Guidelines

### Test Structure
- Use `@MicronautTest` for integration tests
- Use `@WireMockTest` for mocking external services
- Use RestAssured for testing REST endpoints
- Follow Arrange-Act-Assert pattern

### Test Naming
- Use backticks for test names: `` `test description with spaces` ``
- Be descriptive about what is being tested
- Include the expected outcome

### Coverage Requirements
- Maintain at least 80% code coverage
- Focus on business logic and critical paths
- Test edge cases and error conditions

**Example Test:**
```kotlin
@WireMockTest(httpPort = 8091)
@MicronautTest
class TenantApiTest {
    
    @Test
    fun `should return tenant by id when tenant exists`(
        spec: RequestSpecification
    ) {
        // Arrange
        val tenantId = UUID.randomUUID()
        
        // Act & Assert
        spec.`when`()
            .auth().oauth2("dummy")
            .get("/api/tenant/$tenantId")
        .then()
            .statusCode(200)
            .body("id", equalTo(tenantId.toString()))
    }
}
```

## API Design

### REST Conventions
- Use plural nouns for collection resources (`/tenants`, `/contacts`)
- Use HTTP methods appropriately (GET, POST, PUT, DELETE)
- Return 201 for successful creation with Location header
- Return 204 for successful deletion
- Return 404 when resource not found
- Return 400 for validation errors

### OpenAPI Documentation
Always include OpenAPI annotations:
```kotlin
@Operation(
    summary = "Get tenant by ID",
    description = "Retrieves a tenant's details by their unique identifier"
)
@ApiResponse(responseCode = "200", description = "Tenant found")
@ApiResponse(responseCode = "404", description = "Tenant not found")
@Get("/{id}")
fun getTenant(@PathVariable id: UUID): TenantResource
```

## Security

### Authentication
- Use Hanko for authentication
- Validate JWT tokens
- Use `@Secured` annotation for endpoint security
- Don't expose sensitive information in API responses

### Input Validation
- Use Micronaut validation annotations (`@NotBlank`, `@Size`, `@Valid`)
- Validate all user inputs
- Sanitize data before database operations

## Configuration

### Application Properties
- Use `application.yml` for configuration
- Support environment-specific configs (dev, test, prod)
- Use `@Property` or `@Value` to inject configuration values

### Environment Variables
- Use environment variables for sensitive data
- Document required environment variables
- Provide sensible defaults where appropriate

## Common Pitfalls to Avoid

- Don't block the event loop (use `@ExecuteOn(TaskExecutors.IO)` for blocking operations)
- Don't create circular dependencies
- Don't use reflection when compile-time solutions exist
- Don't ignore null safety
- Don't commit hardcoded credentials or secrets
- Don't modify production data in tests

## Performance Considerations

- Use connection pooling (HikariCP is configured)
- Optimize database queries (avoid N+1 queries)
- Use pagination for large result sets
- Cache frequently accessed data when appropriate
- Use reactive types (Reactor) for asynchronous operations when needed

## Logging

- Use SLF4J for logging
- Log at appropriate levels (TRACE, DEBUG, INFO, WARN, ERROR)
- Include context in log messages
- Don't log sensitive information
- Use structured logging when possible

**Example:**
```kotlin
private val log = LoggerFactory.getLogger(TenantApiController::class.java)

log.info("Processing tenant request for id: {}", tenantId)
log.error("Failed to process tenant", exception)
```

## Resources

- [Micronaut Documentation](https://docs.micronaut.io/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Micronaut Data Documentation](https://micronaut-projects.github.io/micronaut-data/latest/guide/)
