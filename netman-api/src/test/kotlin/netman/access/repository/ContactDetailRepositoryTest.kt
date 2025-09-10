package netman.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactDetailRepositoryTest() : RepositoryTestBase() {

    @Inject
    private lateinit var contactDetailRepository: ContactDetailRepository;
    @Inject
    private lateinit var contactRepository: ContactRepository
    @Inject
    private lateinit var tenantRepository: TenantRepository

    @Test
    fun `test save and find contact detail by contact id`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent = """{"test": "test"}"""
        val type = "testtype"

        // Act
        val contactDetail = contactDetailRepository.save(
            ContactDetailDTO(contactId = contact.id!!, type = type, detail = jsonContent))

        val fetchedContactDetail = contactDetailRepository.getByContactId(contact.id)

        // Assert
        assertThat(fetchedContactDetail).hasSize(1)
        assertThat(fetchedContactDetail.single()).isEqualTo(contactDetail)
        assertThat(fetchedContactDetail.single().type).isEqualTo(type)
        assertThat(fetchedContactDetail.single().detail).isEqualTo(jsonContent)
    }

    @Test
    fun `add and fetch multiple contactDetails`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent1 = """{"test": "test"}"""
        val jsonContent2 = """{"test2": "test2"}"""
        val type1 = "testtype1"
        val type2 = "testtype2"

        // Act
        val contactDetail1 = contactDetailRepository.save(
            ContactDetailDTO(contactId = contact.id!!, type = type1, detail = jsonContent1))
        val contactDetail2 = contactDetailRepository.save(
            ContactDetailDTO(contactId = contact.id, type = type2, detail = jsonContent2))

        val fetchedContactDetails = contactDetailRepository.getByContactId(contact.id)

        // Assert
        assertThat(fetchedContactDetails).hasSize(2)
        assertThat(fetchedContactDetails).hasSameElementsAs(listOf(contactDetail1, contactDetail2))
    }

    @Test
    fun `add and update contact detail`() {
        // Arrange
        val contact = prepareContact()
        val jsonContent1 = """{"test": "test"}"""
        val jsonContect2 = """{"test2": "test2"}"""
        val type = "type"

        // Act
        val contactDetail = contactDetailRepository.save(ContactDetailDTO(contactId = contact.id!!, type = type, detail = jsonContent1))
        val contactDetail2 = contactDetailRepository.update(contactDetail.copy(detail = jsonContect2))
        val fetchedContactDetail = contactDetailRepository.getByContactId(contact.id)

        // Assert
        assertThat(fetchedContactDetail).hasSize(1)
        throw Exception("Test not done implementing ")
    }

    private fun prepareContact() : ContactDTO {
        val tenant = tenantRepository.save(TenantDTO(name = "test tenant", type = "type"))
        return contactRepository.save(ContactDTO(tenantId = tenant.id!!, name = "test"))
    }
}