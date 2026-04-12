package netman.access

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import netman.models.Contact
import netman.models.Interaction
import netman.models.InteractionType
import netman.models.Email
import netman.models.Note
import netman.models.Phone
import netman.models.TenantType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.util.*

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactAccessTest : DefaultTestProperties() {

    @Inject
    lateinit var contactAccess: ContactAccess

    @Inject
    lateinit var tenantAccess: TenantAccess

    @Test fun `save contact2 and check that it gets assigned an ID`() {
        // Arrange
        val user1 = "user-id-1234"
        val tenant = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, user1)
        val contact = Contact(name = "Ola Normann", details = listOf(
            Email("ola.norman@test.com", false, "home"),
            Note("test note")))

        // Act
        val savedContact = contactAccess.saveContact(tenant.id, contact)

        // Assert
        assertThat(savedContact.id).isNotNull
        assertThat(savedContact.name).isEqualTo("Ola Normann")
        assertThat(savedContact.details).hasSize(2)
    }

    @Test fun `save contact2 and check view projection`() {
        // Arrange
        val user = "abc-123"
        val tenant = tenantAccess.registerNewTenant("atenant", TenantType.PERSONAL, user)
        val contact1 = Contact(name = "Ola Normann", details = listOf(
            Email("ola.normann@test.com", isPrimary = true, "home"),
        ))
        val contact = Contact(name = "Kari Normann", details = listOf(
            Phone("123456789", "work")
        ))

        // Act
        contactAccess.saveContact(tenant.id, contact1)
        contactAccess.saveContact(tenant.id, contact)

        val contactList = contactAccess.listContacts(tenant.id)

        // Assert
        assertThat(contactList).hasSize(2)
        assertThat(contactList).anySatisfy { c -> assertThat(c.name).isEqualTo("Ola Normann") }
        assertThat(contactList).anySatisfy { c -> assertThat(c.name).isEqualTo("Kari Normann") }
    }
    
    @Test fun `save interaction and check that it gets assigned an ID`() {
        // Arrange
        val user1 = "user-id-1234"
        val tenant = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, user1)
        val contact = contactAccess.saveContact(tenant.id, Contact(name = "John Doe", details = listOf()))
        requireNotNull(contact.id)
        
        val interaction = Interaction(
            contactId = contact.id,
            type = InteractionType.EMAIL,
            content = "Sent an email regarding the project",
            timestamp = Instant.now(),
            metadata = mapOf("subject" to "Project Update")
        )

        // Act
        val savedInteraction = contactAccess.saveInteraction(interaction)

        // Assert
        assertThat(savedInteraction.id).isNotNull
        assertThat(savedInteraction.contactId).isEqualTo(contact.id)
        assertThat(savedInteraction.type).isEqualTo(InteractionType.EMAIL)
        assertThat(savedInteraction.content).isEqualTo("Sent an email regarding the project")
        assertThat(savedInteraction.metadata).containsEntry("subject", "Project Update")
    }
    
    @Test fun `save and retrieve multiple interactions for a contact`() {
        // Arrange
        val user = "abc-123"
        val tenant = tenantAccess.registerNewTenant("atenant", TenantType.PERSONAL, user)
        val contact = contactAccess.saveContact(tenant.id, Contact(name = "Jane Doe", details = listOf()))
        requireNotNull(contact.id)
        
        val interaction1 = Interaction(
            contactId = contact.id,
            type = InteractionType.EMAIL,
            content = "First email",
            timestamp = Instant.now(),
            metadata = mapOf("subject" to "Hello")
        )
        
        val interaction2 = Interaction(
            contactId = contact.id,
            type = InteractionType.CALL,
            content = "Discussed project details",
            timestamp = Instant.now(),
            metadata = mapOf("duration" to "15 minutes")
        )

        // Act
        contactAccess.saveInteraction(interaction1)
        contactAccess.saveInteraction(interaction2)
        
        val interactions = contactAccess.getInteractions(contact.id)

        // Assert
        assertThat(interactions).hasSize(2)
        assertThat(interactions).anySatisfy { c ->
            assertThat(c.type).isEqualTo(InteractionType.EMAIL)
            assertThat(c.content).isEqualTo("First email")
        }
        assertThat(interactions).anySatisfy { c ->
            assertThat(c.type).isEqualTo(InteractionType.CALL)
            assertThat(c.content).isEqualTo("Discussed project details")
        }
    }
    
    @Test fun `save interaction with empty metadata`() {
        // Arrange
        val user = "user-id-999"
        val tenant = tenantAccess.registerNewTenant("tenant-999", TenantType.PERSONAL, user)
        val contact = contactAccess.saveContact(tenant.id, Contact(name = "Bob Smith", details = listOf()))
        requireNotNull(contact.id)
        
        val interaction = Interaction(
            contactId = contact.id,
            type = InteractionType.TEXT_MESSAGE,
            content = "Quick text message",
            timestamp = Instant.now(),
            metadata = emptyMap()
        )

        // Act
        val savedInteractions = contactAccess.saveInteraction(interaction)

        // Assert
        assertThat(savedInteractions.id).isNotNull
        assertThat(savedInteractions.metadata).isEmpty()
    }

    @Test fun `saveInteraction should update existing interactions`() {
        // Arrange
        val user = "user-update-1"
        val tenant = tenantAccess.registerNewTenant("tenant-update", TenantType.PERSONAL, user)
        val contact = contactAccess.saveContact(tenant.id, Contact(name = "Update Test", details = listOf()))
        requireNotNull(contact.id)

        val initialInteraction = Interaction(
            contactId = contact.id,
            type = InteractionType.EMAIL,
            content = "Initial content",
            timestamp = Instant.now(),
            metadata = mapOf("key" to "value")
        )

        val savedInteraction = contactAccess.saveInteraction(initialInteraction)
        val originalId = savedInteraction.id
        requireNotNull(originalId)

        val updatedInteraction = savedInteraction.copy(
            content = "Updated content",
            metadata = mapOf("key" to "new value", "other" to "new item")
        )

        // Act
        val result = contactAccess.saveInteraction(updatedInteraction)

        // Assert
        assertThat(result.id).isEqualTo(originalId)
        assertThat(result.content).isEqualTo("Updated content")
        assertThat(result.metadata).containsEntry("key", "new value")
        assertThat(result.metadata).containsEntry("other", "new item")

        // Verify retrieval
        val retrieved = contactAccess.getInteractions(contact.id).first { it.id == originalId }
        assertThat(retrieved.content).isEqualTo("Updated content")
        assertThat(retrieved.metadata).containsEntry("key", "new value")
    }

    @Test fun `saveInteraction should save new interactions with provided ID`() {
        // Arrange
        val user = "user-provided-id"
        val tenant = tenantAccess.registerNewTenant("tenant-provided-id", TenantType.PERSONAL, user)
        val contact = contactAccess.saveContact(tenant.id, Contact(name = "ID Test", details = listOf()))
        requireNotNull(contact.id)

        val providedId = UUID.randomUUID()
        val interaction = Interaction(
            id = providedId,
            contactId = contact.id,
            type = InteractionType.CALL,
            content = "Provided ID content",
            timestamp = Instant.now()
        )

        // Act
        val result = contactAccess.saveInteraction(interaction)

        // Assert
        assertThat(result.id).isEqualTo(providedId)
        
        // Verify retrieval
        val retrieved = contactAccess.getInteractions(contact.id).first { it.id == providedId }
        assertThat(retrieved.content).isEqualTo("Provided ID content")
    }

}