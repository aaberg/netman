package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.ActionAccess
import netman.access.ContactAccess
import netman.access.TenantAccess
import netman.access.repository.DefaultTestProperties
import netman.models.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

@MicronautTest(startApplication = false)
class NetworkManagerPendingActionsTest : DefaultTestProperties() {

    @Inject
    lateinit var networkManager: NetworkManager

    @Inject
    lateinit var actionAccess: ActionAccess

    @Inject
    lateinit var contactAccess: ContactAccess

    @Inject
    lateinit var tenantAccess: TenantAccess

    @Inject
    lateinit var membershipManager: MembershipManager

    private data class TestUserData(val userId: UUID, val tenantId: Long)

    private fun createTestUser(): TestUserData {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return TestUserData(UUID.fromString(userId), tenant.id)
    }

    @Test
    fun `runPendingActions should process overdue follow-up actions`() {
        // Arrange
        val testUser = createTestUser()
        
        // Create a contact
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "John Doe",
            details = listOf(
                Email("john.doe@example.com", false, "Work email"),
                Phone("+1234567890", "Work phone")
            )
        )
        contactAccess.saveContact(testUser.tenantId, contact)
        
        // Create a follow-up action that is overdue (trigger time in the past)
        val pastTime = Instant.now().minusSeconds(3600) // 1 hour ago
        val action = actionAccess.registerNewAction(
            tenantId = testUser.tenantId,
            command = CreateFollowUpCommand(contact.id!!, "Test follow-up note"),
            triggerTime = pastTime,
            frequency = Frequency.Single
        )
        
        // Verify the action was created as pending
        val pendingAction = actionAccess.getAction(testUser.tenantId, action.id)
        assertThat(pendingAction).isNotNull
        assertThat(pendingAction?.status).isEqualTo(ActionStatus.Pending)
        assertThat(pendingAction?.triggerTime?.isBefore(Instant.now())).isTrue()
        
        // Act - run pending actions
        networkManager.runPendingActions()
        
        // Assert
        // Check that the action is now completed
        val completedAction = actionAccess.getAction(testUser.tenantId, action.id)
        assertThat(completedAction).isNotNull
        assertThat(completedAction?.status).isEqualTo(ActionStatus.Completed)
        
        // Check that a follow-up was registered
        val followUps = actionAccess.getFollowUps(testUser.tenantId, FollowUpStatus.Pending, io.micronaut.data.model.Pageable.from(0, 10))
        assertThat(followUps.size).isGreaterThan(0)
        
        val followUp = followUps.first()
        assertThat(followUp.contactId).isEqualTo(contact.id)
        assertThat(followUp.note).isEqualTo("Test follow-up note")
        // taskId should be a random UUID since no actual task is created
        assertThat(followUp.taskId).isNotNull()
    }

    @Test
    fun `runPendingActions should not process actions that are not yet due`() {
        // Arrange
        val testUser = createTestUser()
        
        // Create a contact
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Jane Smith",
            details = listOf(
                Email("jane.smith@example.com", false, "Work email"),
                Phone("+1987654321", "Work phone")
            )
        )
        contactAccess.saveContact(testUser.tenantId, contact)
        
        // Create a follow-up action that is not yet due (trigger time in the future)
        val futureTime = Instant.now().plusSeconds(3600) // 1 hour from now
        val action = actionAccess.registerNewAction(
            tenantId = testUser.tenantId,
            command = CreateFollowUpCommand(contact.id!!, "Future follow-up note"),
            triggerTime = futureTime,
            frequency = Frequency.Single
        )
        
        // Verify the action was created as pending
        val pendingAction = actionAccess.getAction(testUser.tenantId, action.id)
        assertThat(pendingAction).isNotNull
        assertThat(pendingAction?.status).isEqualTo(ActionStatus.Pending)
        assertThat(pendingAction?.triggerTime?.isAfter(Instant.now())).isTrue()
        
        // Act - run pending actions
        networkManager.runPendingActions()
        
        // Assert
        // Check that the action is still pending (not processed)
        val unchangedAction = actionAccess.getAction(testUser.tenantId, action.id)
        assertThat(unchangedAction).isNotNull
        assertThat(unchangedAction?.status).isEqualTo(ActionStatus.Pending)
        
        // Check that no follow-up was registered
        val followUps = actionAccess.getFollowUps(testUser.tenantId, FollowUpStatus.Pending, io.micronaut.data.model.Pageable.from(0, 10))
        assertThat(followUps.isEmpty()).isTrue()
    }

    @Test
    fun `runPendingActions should create new action for recurring follow-ups`() {
        // Arrange
        val testUser = createTestUser()
        
        // Create a contact
        val contact = Contact2(
            id = UUID.randomUUID(),
            name = "Recurring Contact",
            details = listOf(
                Email("recurring@example.com", false, "Work email"),
                Phone("+1112223333", "Work phone")
            )
        )
        contactAccess.saveContact(testUser.tenantId, contact)
        
        // Create a recurring follow-up action that is overdue (trigger time in the past)
        val pastTime = Instant.now().minusSeconds(3600) // 1 hour ago
        val action = actionAccess.registerNewAction(
            tenantId = testUser.tenantId,
            command = CreateFollowUpCommand(contact.id!!, "Recurring follow-up note"),
            triggerTime = pastTime,
            frequency = Frequency.Weekly // Recurring weekly
        )
        
        // Verify the action was created as pending
        val pendingAction = actionAccess.getAction(testUser.tenantId, action.id)
        assertThat(pendingAction).isNotNull
        assertThat(pendingAction?.status).isEqualTo(ActionStatus.Pending)
        assertThat(pendingAction?.frequency).isEqualTo(Frequency.Weekly)
        
        // Act - run pending actions
        networkManager.runPendingActions()
        
        // Assert
        // Check that the original action is now completed
        val completedAction = actionAccess.getAction(testUser.tenantId, action.id)
        assertThat(completedAction).isNotNull
        assertThat(completedAction?.status).isEqualTo(ActionStatus.Completed)
        
        // Check that a follow-up was registered
        val followUps = actionAccess.getFollowUps(testUser.tenantId, FollowUpStatus.Pending, io.micronaut.data.model.Pageable.from(0, 10))
        assertThat(followUps.size).isGreaterThan(0)
        
        val followUp = followUps.first()
        assertThat(followUp.contactId).isEqualTo(contact.id)
        assertThat(followUp.note).isEqualTo("Recurring follow-up note")
        
        // Check that a new recurring action was created for the next occurrence
        val allActions = actionAccess.getActions(testUser.tenantId, ActionStatus.Pending, null, io.micronaut.data.model.Pageable.from(0, 10))
        assertThat(allActions.size).isGreaterThan(0)
        
        val newAction = allActions.first { it.id != action.id }
        assertThat(newAction.status).isEqualTo(ActionStatus.Pending)
        assertThat(newAction.frequency).isEqualTo(Frequency.Weekly)
        assertThat(newAction.triggerTime.isAfter(pastTime)).isTrue()
        
        // Verify the new action is scheduled approximately 1 week later
        val expectedNextTime = pastTime.plusSeconds(7 * 24 * 60 * 60)
        val timeDifference = java.time.Duration.between(newAction.triggerTime, expectedNextTime).abs().seconds
        assertThat(timeDifference).isLessThan(60) // Allow 1 minute tolerance
    }
}