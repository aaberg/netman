package netman.businesslogic.models

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import netman.models.*
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("primary@example.com", result.contactInfo)
        assertEquals("Email", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("email1@example.com", result.contactInfo)
        assertEquals("Email", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("+1234567890", result.contactInfo)
        assertEquals("Phone", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("", result.contactInfo)
        assertEquals("", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("email2@example.com", result.contactInfo)
        assertEquals("Email", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("primary@example.com", result.contactInfo)
        assertEquals("Email", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(specificId, result.id)
        assertEquals("Test User", result.name)
        assertEquals("TU", result.initials) // Initials should be generated from name
        assertEquals("test@example.com", result.contactInfo)
        assertEquals("Email", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
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
        assertEquals(contact.id, result.id)
        assertEquals(contact.name, result.name)
        assertEquals(contact.initials, result.initials)
        assertEquals("", result.contactInfo)
        assertEquals("", result.contactInfoIcon)
        assertEquals("", result.labels)
        assertEquals(false, result.hasUpdates)
    }
}