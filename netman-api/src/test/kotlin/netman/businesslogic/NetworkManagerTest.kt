package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.TenantAccess
import netman.access.repository.DefaultTestProperties
import netman.models.Contact
import netman.models.ContactDetail
import netman.models.ContactWithDetails
import netman.models.Email
import netman.models.Phone
import netman.models.Tenant
import netman.models.TenantType
import netman.models.newContact
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
        assertThat(fetchedContacts).hasSameElementsAs(createdContacts)
    }

    @Test
    fun `save and fetch a contact with details`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val email = ContactDetail(detail = Email("test@test.com", false, "dummy"))
        val phone = ContactDetail(detail = Phone("11111111", "phone" ))

        // Act
        val savedContact = networkManager.saveContactWithDetails(tenant.id,
            ContactWithDetails(newContact("Ola Normann"), listOf(email, phone)))

        val fetchedContact = networkManager.getContactWithDetails(userId, tenant.id, savedContact.contact.id!!)

        // Assert
        assertThat(fetchedContact).isEqualTo(savedContact)
    }

    @Test
    fun `save and update contact`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)

        // Act
        val contactWDetail = networkManager.saveContactWithDetails(tenant.id,
            ContactWithDetails(newContact("Ola Normann"), listOf()))

        val updatedContactWDetail = networkManager.saveContactWithDetails(tenant.id,
            contactWDetail.copy(contact =  contactWDetail.contact.copy(name = "new name")))

        val myContacts = networkManager.getMyContacts(userId, tenant.id)

        // Assert
        assertThat(updatedContactWDetail.contact.name).isEqualTo("new name")
        assertThat(myContacts).hasSize(1)
    }

    @Test
    fun `save a contact with empty name is expected to throw`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)

        // Act & Assert
        val validationException = assertThrows<ValidationException> {
            networkManager.saveContactWithDetails(tenant.id, ContactWithDetails(newContact(""), listOf()))
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