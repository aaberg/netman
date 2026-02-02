package netman.businesslogic.models

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import netman.models.*
import org.assertj.core.api.Assertions.assertThat
import java.util.UUID

@MicronautTest
class ContactResourceMapperTest {

    @Inject
    lateinit var mapper: ContactResourceMapper

    @Test
    fun `test mapToListItem with primary email`() {
        // Given
        val primaryEmail = Email("primary@example.com", isPrimary = true, label = "Work")
        val secondaryEmail = Email("secondary@example.com", isPrimary = false, label = "Personal")
        val phone = Phone("+1234567890", label = "Mobile")
        
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "John Doe",
            details = listOf(primaryEmail, secondaryEmail, phone)
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isEqualTo("primary@example.com")
        assertThat(result.contactInfoIcon).isEqualTo("Email")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem with non-primary email only`() {
        // Given
        val email1 = Email("email1@example.com", isPrimary = false, label = "Work")
        val email2 = Email("email2@example.com", isPrimary = false, label = "Personal")
        
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Jane Smith",
            details = listOf(email1, email2)
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isEqualTo("email1@example.com")
        assertThat(result.contactInfoIcon).isEqualTo("Email")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem with phone only`() {
        // Given
        val phone1 = Phone("+1234567890", label = "Mobile", isPrimary = true)
        val phone2 = Phone("+0987654321", label = "Work")
        
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Bob Johnson",
            details = listOf(phone1, phone2)
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isEqualTo("+1234567890")
        assertThat(result.contactInfoIcon).isEqualTo("Phone")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem with no contact info`() {
        // Given
        val note = Note("This is a note")
        val workInfo = WorkInfo("Developer", "Engineering", "Tech Corp")
        
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Alice Williams",
            details = listOf(note, workInfo)
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isEqualTo("")
        assertThat(result.contactInfoIcon).isEqualTo("")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem with multiple emails prioritizing primary`() {
        // Given
        val email1 = Email("email1@example.com", isPrimary = false, label = "Work")
        val email2 = Email("email2@example.com", isPrimary = true, label = "Personal")
        val email3 = Email("email3@example.com", isPrimary = false, label = "Other")
        
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Charlie Brown",
            details = listOf(email1, email2, email3)
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isEqualTo("email2@example.com")
        assertThat(result.contactInfoIcon).isEqualTo("Email")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem with mixed contact info prioritizing primary email`() {
        // Given
        val phone = Phone("+1234567890", label = "Mobile")
        val primaryEmail = Email("primary@example.com", isPrimary = true, label = "Work")
        val secondaryEmail = Email("secondary@example.com", isPrimary = false, label = "Personal")
        
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Diana Prince",
            details = listOf(phone, primaryEmail, secondaryEmail)
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isEqualTo("primary@example.com")
        assertThat(result.contactInfoIcon).isEqualTo("Email")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem field mapping`() {
        // Given
        val specificId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        val contact = Contact2(
            id = specificId,
            name = "Test User",
            details = listOf(Email("test@example.com", isPrimary = true, label = "Work"))
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then - Verify all fields are correctly mapped
        assertThat(result.id).isEqualTo(specificId)
        assertThat(result.name).isEqualTo("Test User")
        assertThat(result.initials).isEqualTo("TU") // Initials should be generated from name
        assertThat(result.contactInfo).isEqualTo("test@example.com")
        assertThat(result.contactInfoIcon).isEqualTo("Email")
        assertThat(result.labels).isEqualTo("")
        assertThat(result.hasUpdates).isFalse()
    }

    @Test
    fun `test mapToListItem with empty details list`() {
        // Given
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Empty Contact",
            details = emptyList()
        )

        // When
        val result = mapper.mapToListItem(contact)

        // Then
        assertThat(result.id).isEqualTo(contact.id)
        assertThat(result.name).isEqualTo(contact.name)
        assertThat(result.initials).isEqualTo(contact.initials)
        assertThat(result.contactInfo).isBlank()
        assertThat(result.contactInfoIcon).isBlank()
        assertThat(result.labels).isBlank()
        assertThat(result.hasUpdates).isFalse()
    }
}