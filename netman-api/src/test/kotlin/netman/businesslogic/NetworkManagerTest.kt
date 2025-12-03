package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.TenantAccess
import netman.access.repository.DefaultTestProperties
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
        assertThat(fetchedContacts).allSatisfy { c -> createdContacts.any { it.id == c.contactId } }
    }

    @Test
    fun `save and fetch a contact with details`() {
        // Arrange
        val userId = "testuser_id"
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val email = Email("test@test.com", false, "dummy")
        val phone = Phone("11111111", "phone")


        // Act
        val savedContact = networkManager.saveContactWithDetails(userId, tenant.id,
            newContact("Ola Normann", listOf(email, phone)))

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
        val contactWDetail = networkManager.saveContactWithDetails(userId,tenant.id,
            newContact("Ola Normann", listOf()))

        val updatedContactWDetail = networkManager.saveContactWithDetails(userId,tenant.id,
            contactWDetail.copy(name = "new name"))

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
            networkManager.saveContactWithDetails(userId, tenant.id, newContact("", listOf()))
        }

        println(validationException.message)
    }

    @Test
    fun `create task with trigger successfully`() {
        // Arrange
        val userUuid = createTestUser()
        val userId = userUuid.toString()
        val contactId = java.util.UUID.randomUUID()
        val task = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "Follow up with client"),
            status = TaskStatus.Pending
        )
        val triggerTime = java.time.Instant.now().plusSeconds(3600)
        val trigger = Trigger(
            triggerType = "scheduled",
            triggerTime = triggerTime,
            targetTaskId = java.util.UUID.randomUUID(), // Will be replaced with saved task ID
            status = TriggerStatus.Pending,
            statusTime = java.time.Instant.now()
        )

        // Act
        val savedTask = networkManager.createTaskWithTrigger(userId, task, trigger)

        // Assert
        assertThat(savedTask.id).isNotNull
        assertThat(savedTask.userId).isEqualTo(userUuid)
        assertThat(savedTask.status).isEqualTo(TaskStatus.Pending)
        val followUpTask = savedTask.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Follow up with client")
    }

    @Test
    fun `create task without trigger successfully`() {
        // Arrange
        val userUuid = createTestUser()
        val userId = userUuid.toString()
        val contactId = java.util.UUID.randomUUID()
        val task = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "No trigger task"),
            status = TaskStatus.Pending
        )

        // Act
        val savedTask = networkManager.createTaskWithTrigger(userId, task, null)

        // Assert
        assertThat(savedTask.id).isNotNull
        assertThat(savedTask.userId).isEqualTo(userUuid)
        assertThat(savedTask.status).isEqualTo(TaskStatus.Pending)
    }

    @Test
    fun `list pending and due tasks for user`() {
        // Arrange
        val userUuid = createTestUser()
        val userId = userUuid.toString()
        val contactId = java.util.UUID.randomUUID()
        
        // Create some tasks with different statuses
        val pendingTask = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "Pending task"),
            status = TaskStatus.Pending
        )
        val dueTask = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "Due task"),
            status = TaskStatus.Due
        )
        val completedTask = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "Completed task"),
            status = TaskStatus.Completed
        )

        networkManager.createTaskWithTrigger(userId, pendingTask, null)
        networkManager.createTaskWithTrigger(userId, dueTask, null)
        networkManager.createTaskWithTrigger(userId, completedTask, null)

        // Act
        val tasks = networkManager.listPendingAndDueTasks(userId)

        // Assert
        assertThat(tasks).hasSize(2)
        assertThat(tasks).allSatisfy { task ->
            task.status == TaskStatus.Pending || task.status == TaskStatus.Due
        }
        val notes = tasks.map { (it.data as FollowUpTask).note }
        assertThat(notes).containsExactlyInAnyOrder("Pending task", "Due task")
    }

    @Test
    fun `list pending due triggers`() {
        // Arrange
        val userUuid = createTestUser()
        val userId = userUuid.toString()
        val contactId = java.util.UUID.randomUUID()
        val now = java.time.Instant.now()
        
        // Create tasks
        val task1 = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "Task 1"),
            status = TaskStatus.Pending
        )
        val task2 = Task(
            userId = userUuid,
            data = FollowUpTask(contactId = contactId, note = "Task 2"),
            status = TaskStatus.Pending
        )

        val savedTask1 = networkManager.createTaskWithTrigger(userId, task1, null)
        val savedTask2 = networkManager.createTaskWithTrigger(userId, task2, null)

        // Create triggers with different times and statuses
        val dueTrigger = Trigger(
            triggerType = "scheduled",
            triggerTime = now.minusSeconds(3600), // Past time
            targetTaskId = savedTask1.id!!,
            status = TriggerStatus.Pending,
            statusTime = now
        )
        val futureTrigger = Trigger(
            triggerType = "scheduled",
            triggerTime = now.plusSeconds(3600), // Future time
            targetTaskId = savedTask2.id!!,
            status = TriggerStatus.Pending,
            statusTime = now
        )

        networkManager.createTaskWithTrigger(userId, task1, dueTrigger)
        networkManager.createTaskWithTrigger(userId, task2, futureTrigger)

        // Act
        val dueTriggers = networkManager.listPendingDueTriggers()

        // Assert - Should only include triggers with past triggerTime
        assertThat(dueTriggers).isNotEmpty
        assertThat(dueTriggers).allSatisfy { trigger ->
            trigger.status == TriggerStatus.Pending && trigger.triggerTime.isBefore(now)
        }
    }

    private fun createTestUser(): java.util.UUID {
        val userId = java.util.UUID.randomUUID().toString()
        membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return java.util.UUID.fromString(userId)
    }

    private fun createTenantWithContacts(userId: String = "dummy") : TenantContactTuple {
        val tenant = tenantAccess.registerNewTenant("test", TenantType.PERSONAL, userId)
        val contact1 = contactAccess.saveContact(tenant.id, newContact("Ola Normann"))
        val contact2 = contactAccess.saveContact(tenant.id, newContact("Kari Normann"))

        return TenantContactTuple(listOf(contact1, contact2), tenant)
    }
    data class TenantContactTuple(val contacts: List<Contact2>, val tenant: Tenant)
}