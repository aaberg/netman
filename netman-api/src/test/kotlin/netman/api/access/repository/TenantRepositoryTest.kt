package netman.api.access.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNotNull
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantRepositoryTest : TestPropertyProvider {

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

    @Inject
    lateinit var tenantRepository: TenantRepository

    @Test
    fun `save and find tenant by id`() {
        val saved = tenantRepository.save(TenantDTO(
            name = "Acme Corp",
            type = "Organization"
        ))
        assertNotNull(saved.id)

        val found = tenantRepository.getById(saved.id!!)
        assertTrue(found.isPresent)

        val tenant = found.get()
        assertEquals("Acme Corp", tenant.name)
        assertEquals("Organization", tenant.type)
    }

    @Test
    fun `rename tenant`() {
        // Arrange
        val tenant = tenantRepository.save(TenantDTO(
            name = "Acme Corp",
            type = "Organization"
        ))

        // act
        tenantRepository.update(tenant.copy(name = "Acme Inc"))

        // Assert
        val found = tenantRepository.getById(tenant.id!!)
        assertTrue(found.isPresent)
        assertEquals("Acme Inc", found.get().name)
    }


}