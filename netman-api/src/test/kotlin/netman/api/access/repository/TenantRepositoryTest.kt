package netman.api.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNotNull

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantRepositoryTest : RepositoryTestBase() {

    @Inject
    lateinit var tenantRepository: TenantRepository

    @Test
    fun `save and find tenant by id`() {
        val saved = tenantRepository.save(TenantDTO(
            name = "Acme Corp",
            type = "Organization"
        ))
        assertNotNull(saved.id)

        val tenant = tenantRepository.getById(saved.id!!)
        assertTrue(tenant != null)

        assertEquals("Acme Corp", tenant?.name)
        assertEquals("Organization", tenant?.type)
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
        assertTrue(found != null)
        assertEquals("Acme Inc", found?.name)
    }

    @Test
    fun `associate multiple tenants with member and fetch them`() {
        // Arrange
        val userId = "user-id-123"
        val tenant1 = tenantRepository.save(TenantDTO(name = "Acme Corp", type = "Organization"))
        val tenant2 = tenantRepository.save(TenantDTO(name = "Acme Inc", type = "Organization"))
        tenantRepository.addMemberToTenant(userId, tenant1.id!!, "Member")
        tenantRepository.addMemberToTenant(userId, tenant2.id!!, "Member")

        // Act
        val userTenants = tenantRepository.findAllByUserId(userId)

        // Assert
        assertThat(userTenants).hasSize(2)
        assertThat(userTenants).containsExactlyInAnyOrder(tenant1, tenant2)
    }


}