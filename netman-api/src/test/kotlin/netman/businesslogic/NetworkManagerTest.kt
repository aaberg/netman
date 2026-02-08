package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ValidationException
import netman.access.ActionAccess
import netman.access.ContactAccess
import netman.access.TenantAccess
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.models.ContactResource
import netman.businesslogic.models.CreateFollowUpTaskRequest
import netman.businesslogic.models.CreateTriggerRequest
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
    private lateinit var actionAccess: ActionAccess
    
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
        val email = Email("test@test.com", false, "dummy")
        val phone = Phone("11111111", "phone")


        // Act
        val contactResource = ContactResource(name = "Ola Normann", details = listOf(email, phone))
        val savedContact = networkManager.saveContactWithDetails(userId, tenant.id, contactResource)

        val fetchedContact = networkManager.getContactWithDetails(userId, tenant.id, savedContact.id!!)

        // Assert
        assertThat(fetchedContact).isEqualTo(savedContact)
    }

    @Test
    fun `save and update contact`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)

        // Act
        val contactResource = ContactResource(name = "Ola Normann", details = listOf())
        val contactWDetail = networkManager.saveContactWithDetails(userId,tenant.id, contactResource)

        val updatedContactResource = contactWDetail.copy(name = "new name")
        val updatedContactWDetail = networkManager.saveContactWithDetails(userId,tenant.id, updatedContactResource)

        val myContacts = networkManager.getMyContacts(userId, tenant.id)

        // Assert
        assertThat(updatedContactWDetail.name).isEqualTo("new name")
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
            val contactResource = ContactResource(name = "", details = listOf())
            networkManager.saveContactWithDetails(userId, tenant.id, contactResource)
        }

        println(validationException.message)
    }

    @Test
    fun `summariseTenant should return correct summary for tenant`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)
        
        // Create some contacts
        contactAccess.saveContact(tenant.id, newContact("Contact 1"))
        contactAccess.saveContact(tenant.id, newContact("Contact 2"))
        
        // Create some pending actions
        val action1 = actionAccess.registerNewAction(
            tenant.id, 
            CreateFollowUpCommand(java.util.UUID.randomUUID(), "Follow up 1"), 
            java.time.Instant.now().plusSeconds(3600), 
            Frequency.Single
        )
        
        val action2 = actionAccess.registerNewAction(
            tenant.id, 
            CreateFollowUpCommand(java.util.UUID.randomUUID(), "Follow up 2"), 
            java.time.Instant.now().plusSeconds(7200), 
            Frequency.Single
        )
        
        // Create some pending follow-ups
        val followUp1 = actionAccess.registerFollowUp(
            tenant.id, 
            java.util.UUID.randomUUID(), 
            java.util.UUID.randomUUID(), 
            "Follow up note 1"
        )
        
        val followUp2 = actionAccess.registerFollowUp(
            tenant.id, 
            java.util.UUID.randomUUID(), 
            java.util.UUID.randomUUID(), 
            "Follow up note 2"
        )

        // Act
        val summary = networkManager.summariseTenant(userId, tenant.id)

        // Assert
        assertThat(summary.tenantId).isEqualTo(tenant.id)
        assertThat(summary.numberOfContacts).isEqualTo(2)
        assertThat(summary.numberOfPendingActions).isEqualTo(2)
        assertThat(summary.pendingFollowUps).hasSize(2)
        assertThat(summary.pendingFollowUps).anySatisfy { 
            assertThat(it.id).isEqualTo(followUp1.id)
            assertThat(it.note).isEqualTo("Follow up note 1")
        }
        assertThat(summary.pendingFollowUps).anySatisfy { 
            assertThat(it.id).isEqualTo(followUp2.id)
            assertThat(it.note).isEqualTo("Follow up note 2")
        }
    }

    @Test
    fun `summariseTenant should return empty summary for new tenant`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("emptytenant", TenantType.PERSONAL, userId)

        // Act
        val summary = networkManager.summariseTenant(userId, tenant.id)

        // Assert
        assertThat(summary.tenantId).isEqualTo(tenant.id)
        assertThat(summary.numberOfContacts).isEqualTo(0)
        assertThat(summary.numberOfPendingActions).isEqualTo(0)
        assertThat(summary.pendingFollowUps).isEmpty()
    }

    @Test
    fun `summariseTenant should throw when user has no access`() {
        // Arrange
        val userId = "testuser_id"
        val otherUserId = "otheruser_id"
        val tenant = tenantAccess.registerNewTenant("testtenant", TenantType.PERSONAL, userId)

        // Act & Assert
        val ex = assertThrows<ForbiddenException> {
            networkManager.summariseTenant(otherUserId, tenant.id)
        }

        // Assert
        assertThat(ex.message).isEqualTo("User $otherUserId does not have access to tenant ${tenant.id}")
    }

    private data class TestUserData(val userId: java.util.UUID, val tenantId: Long)

    private fun createTestUser(): TestUserData {
        val userId = java.util.UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return TestUserData(java.util.UUID.fromString(userId), tenant.id)
    }

    private fun createTenantWithContacts(userId: String = "dummy") : TenantContactTuple {
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val contact1 = contactAccess.saveContact(tenant.id, newContact("Ola Normann"))
        val contact2 = contactAccess.saveContact(tenant.id, newContact("Kari Normann"))

        return TenantContactTuple(listOf(contact1, contact2), tenant)
    }
    data class TenantContactTuple(val contacts: List<Contact2>, val tenant: Tenant)
}