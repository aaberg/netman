package netman.access

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.RepositoryTestBase
import netman.models.TenantType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactAccessTest : RepositoryTestBase() {

    @Inject
    lateinit var contactAccess: ContactAccess

    @Inject
    lateinit var tenantAccess: TenantAccess

    @Test
    fun `get tenant contacts`() {
        // Arrange
        val user1 = "user-id-1234"
        val tenant = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, user1)

        val contact1 = contactAccess.createContact(tenant.id, "Ola Normann")
        val contact2 = contactAccess.createContact(tenant.id, "Kari Normann")

        assertThat(contact1).isNotNull
            .hasNoNullFieldsOrProperties()
            .hasFieldOrPropertyWithValue("name", "Ola Normann")
        assertThat(contact2).isNotNull
            .hasNoNullFieldsOrProperties()
            .hasFieldOrPropertyWithValue("name", "Kari Normann")

        // Act
        val fetchedContact = contactAccess.getContacts(tenant.id)

        // Assert
        assertThat(fetchedContact).containsExactlyInAnyOrder(contact1, contact2)
    }
}