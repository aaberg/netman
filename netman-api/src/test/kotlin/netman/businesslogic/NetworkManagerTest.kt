package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.TenantAccess
import netman.access.repository.RepositoryTestBase
import netman.models.Tenant
import netman.models.TenantType
import netman.api.contacts.models.Contact
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(startApplication = false)
class NetworkManagerTest : RepositoryTestBase() {

    @Inject
    private lateinit var tenantAccess: TenantAccess
    @Inject
    private lateinit var networkManager: NetworkManager

    private val userId = "testuser_id"

    @Test
    fun `when user with no access tries to fetch contacts it throws`() {
        // Arrange
        val (_, tenant) = createTenantWithContacts()

        // Act
        val ex = assertThrows<ForbiddenException> { -> networkManager.getMyContacts("otherUser", tenant.id) }

        // Assert
        assertThat(ex.message).isEqualTo("User otherUser does not have access to tenant ${tenant.id}")
    }

    @Test
    fun `when getMyContacts called returns as expected`() {
       // Arrange
        val userId = "testuser_id"
        val (createdContacts, tenant) = createTenantWithContacts(userId)

        // Act
        val fetchedContacts =  networkManager.getMyContacts(userId, tenant.id)

        // Assert
        assertThat(fetchedContacts).hasSameElementsAs(createdContacts)
    }

    private fun createTenantWithContacts(userId: String = "dummy") : TenantContactTuple {
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val contact1 = tenantAccess.createContact(tenant.id, "Ola Normann")
        val contact2 = tenantAccess.createContact(tenant.id, "Kari Normann")

        return TenantContactTuple(listOf(contact1, contact2), tenant)
    }
    data class TenantContactTuple(val contacts: List<Contact>, val tenant: Tenant)
}