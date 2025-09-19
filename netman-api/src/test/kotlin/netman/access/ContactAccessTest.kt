package netman.access

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import netman.models.Contact
import netman.models.ContactDetail
import netman.models.Email
import netman.models.Notes
import netman.models.TenantType
import netman.models.newContact
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactAccessTest : DefaultTestProperties() {

    @Inject
    lateinit var contactAccess: ContactAccess

    @Inject
    lateinit var tenantAccess: TenantAccess

    @Test
    fun `get tenant contacts`() {
        // Arrange
        val user1 = "user-id-1234"
        val tenant = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, user1)

        val contact1 = contactAccess.saveContact(tenant.id, newContact("Ola Normann"))
        val contact2 = contactAccess.saveContact(tenant.id, newContact("Kari Normann"))

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

    @Test
    fun `save and get some contact details`() {
        // Arrange
        val tenant = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, "user-id-1234")
        val contact = contactAccess.saveContact(tenant.id, newContact("Ola Normann"))

        // Act
        val contactDetails = listOf(
            ContactDetail(detail = Email("test@user.com", true, "Private email")),
            ContactDetail(detail = Email("test@work.com", false, "Work email")),
            ContactDetail(detail = Notes("test notes"))
        )
        val saveResult = contactAccess.saveDetails(contactId = contact.id!!, contactDetails)

        val fetchResult = contactAccess.getContactDetails(contact.id);

        // Assert
        assertThat(saveResult).hasSize(3)
        assertThat(fetchResult).hasSize(3)
        assertThat(fetchResult).hasSameElementsAs(saveResult)
    }

    @Test
    fun `save and update some contact details`() {
        // Arrange
        val contact = prepareContact()

        // Act
        val emailDetail = contactAccess.saveDetails(
            contact.id!!,
            listOf(ContactDetail(detail =Email("test@domain.com", true, "Private email"))))
            .single()

        val updateEmailDetail = emailDetail.copy(detail = (emailDetail.detail as Email).copy(isPrimary = false))
        contactAccess.saveDetails(contact.id, listOf(updateEmailDetail))
        val fetchedEmailDetail = contactAccess.getContactDetails(contact.id)

        // Assert
        assertThat(fetchedEmailDetail).hasSize(1)
        assertThat(emailDetail.detail.isPrimary).isTrue
        assertThat(emailDetail.detail).isInstanceOf(Email::class.java)
        val fetchedEmail = fetchedEmailDetail.single().detail as Email
        assertThat(fetchedEmail.isPrimary).isFalse


    }

    private fun prepareContact() : Contact {
        val tenant = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, "user-id-1234")
        return contactAccess.saveContact(tenant.id, newContact("Ola Normann"))
    }

}