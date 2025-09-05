package netman.access.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.test.support.TestPropertyProvider
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class RepositoryTestBase : TestPropertyProvider {
    companion object {
        @Container
        @JvmStatic
        private val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:17-alpine")
                .withDatabaseName("netman")
                .withUsername("test")
                .withPassword("test")
    }

    override fun getProperties(): @NonNull Map<String?, String?>? {
        if (!postgres.isRunning) {
            postgres.start()
        }

        return mutableMapOf(
            "datasources.default.url" to postgres.jdbcUrl,
            "datasources.default.username" to postgres.username,
            "datasources.default.password" to postgres.password,
            "datasources.default.driverClassName" to "org.postgresql.Driver",
            "datasources.default.db-type" to "postgres",
            "datasources.default.dialect" to "POSTGRES",
            // Liquibase
            "liquibase.datasources.default.change-log" to "classpath:db/liquibase-changelog.xml",
            "liquibase.datasources.default.enabled" to "true"
        )
    }
}