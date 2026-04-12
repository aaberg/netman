package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.TenantAccess
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.models.ContactDetailsResource
import netman.businesslogic.models.SaveContactRequest
import netman.models.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(startApplication = false)
class NetworkManagerTest : DefaultTestProperties() {

    @Inject
    private lateinit var contactAccess: ContactAccess

    @Inject
    private lateinit var tenantAccess: TenantAccess
    
    @Inject
    private lateinit var membershipManager: MembershipManager

    @Inject
    private lateinit var networkManager: NetworkManager

    @Test
    fun `when user with no access tries to fetch contacts it throws`() {
        // Arrange
        val (_, tenant) = createTenantWithContacts()

        // Act
        val ex = assertThrows<ForbiddenException> {
            networkManager.getMyContacts("otherUser", tenant.id)
        }

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
        assertThat(fetchedContacts).allSatisfy { c -> createdContacts.any { it.id == c.id } }
    }

    @Test
    fun `save and fetch a contact with details`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val email = "test@test.com"
        val phone = "11111111"


        // Act
        val contactDetailsResource = SaveContactRequest(null, "Ola Normann", email, phone, null, null, null)
        val savedContactId = networkManager.saveContact(userId, tenant.id, contactDetailsResource).id

        val fetchedContact = networkManager.getContactDetails(userId, tenant.id, savedContactId)

        // Assert
        assertThat(fetchedContact.id).isEqualTo(fetchedContact.id)
        assertThat(fetchedContact.name).isEqualTo(fetchedContact.name)
        assertThat(fetchedContact.email).isEqualTo(fetchedContact.email)
        assertThat(fetchedContact.phone).isEqualTo(fetchedContact.phone)
    }

    @Test
    fun `save and update contact`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)

        // Act
        val contactDetailsResource = SaveContactRequest(null, "Ola Normann", null, null, null, null, "test")
        val savedContactId = networkManager.saveContact(userId,tenant.id, contactDetailsResource).id

        val updatedContactResource = SaveContactRequest(savedContactId, "new name", null, null, null, null, "test")
        networkManager.saveContact(userId,tenant.id, updatedContactResource)

        val myContacts = networkManager.getMyContacts(userId, tenant.id)

        // Assert
        assertThat(myContacts).hasSize(1)
        assertThat(myContacts.first().name).isEqualTo("new name")
    }

    @Test
    fun `save a contact with empty name is expected to throw`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)

        // Act & Assert
        val validationException = assertThrows<ValidationException> {
            val contactDetailsResource = SaveContactRequest(name = "")
            networkManager.saveContact(userId, tenant.id, contactDetailsResource)
        }

        println(validationException.message)
    }

    private fun createTenantWithContacts(userId: String = "dummy") : TenantContactTuple {
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val contact1 = contactAccess.saveContact(tenant.id, newContact("Ola Normann"))
        val contact2 = contactAccess.saveContact(tenant.id, newContact("Kari Normann"))

        return TenantContactTuple(listOf(contact1, contact2), tenant)
    }
    data class TenantContactTuple(val contacts: List<Contact>, val tenant: Tenant)
}