package netman.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactDetailRepositoryTest : DefaultTestProperties() {

    @Inject
    private lateinit var contactDetailRepository:ContactDetailRepository

    @Inject
    private lateinit var contactRepository: ContactRepository
    @Inject
    private lateinit var tenantRepository: TenantRepository

    @Test
    fun `test save and find contact detail by contact id`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent = """{"test": "test"}"""

        // Act
        val contactDetail = contactDetailRepository.save(
            ContactDetailDTO(contactId = contact.id!!, detail = jsonContent))

        val fetchedContactDetail = contactDetailRepository.findByContactId(contact.id)

        // Assert
        assertThat(fetchedContactDetail).hasSize(1)
        assertThat(fetchedContactDetail.single()).isEqualTo(contactDetail)
        assertThat(fetchedContactDetail.single().detail).isEqualTo(jsonContent)
    }

    @Test
    fun `add and fetch multiple contactDetails`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent1 = """{"test": "test"}"""
        val jsonContent2 = """{"test2": "test2"}"""

        // Act
        val contactDetail1 = contactDetailRepository.save(
            ContactDetailDTO(contactId = contact.id!!, detail = jsonContent1))
        val contactDetail2 = contactDetailRepository.save(
            ContactDetailDTO(contactId = contact.id, detail = jsonContent2))

        val fetchedContactDetails = contactDetailRepository.findByContactId(contact.id)

        // Assert
        assertThat(fetchedContactDetails).hasSize(2)
        assertThat(fetchedContactDetails).hasSameElementsAs(listOf(contactDetail1, contactDetail2))
    }

    @Test
    fun `add and update contact detail`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent1 = """{"test": "test"}"""
        val jsonContent2 = """{"test2": "test2"}"""

        // Act
        val contactDetail = contactDetailRepository.save(ContactDetailDTO(contactId = contact.id!!, detail = jsonContent1))
        val contactDetail2 = contactDetailRepository.update(contactDetail.copy(detail = jsonContent2))
        val fetchedContactDetail = contactDetailRepository.findByContactId(contact.id)

        // Assert
        assertThat(fetchedContactDetail).hasSize(1)
        assertThat(contactDetail2.detail).isEqualTo(jsonContent2)
    }

    @Test
    fun `add contact with invalid json detail exppect throw`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent = "invalid json"

        // Act
        val e = catchThrowable{
            contactDetailRepository.save(ContactDetailDTO(contactId = contact.id!!, detail = jsonContent)) }

        // Assert
        assertThat(e).hasMessageContaining("invalid input syntax for type json")

    }

    private fun prepareContact() : ContactDTO {
        val tenant = tenantRepository.save(TenantDTO(name = "test tenant", type = "type"))
        return contactRepository.save(ContactDTO(tenantId = tenant.id!!, name = "test"))
    }
}