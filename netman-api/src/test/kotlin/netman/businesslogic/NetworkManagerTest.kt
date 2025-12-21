package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.ValidationException
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
    private lateinit var membershipManager: MembershipManager
    
    @Inject
    private lateinit var taskAccess: netman.access.TaskAccess
    
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
    fun `create task with trigger successfully`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = java.util.UUID.randomUUID()
        val triggerTime = java.time.Instant.now().plusSeconds(3600)
        val createTaskRequest = CreateFollowUpTaskRequest(
            FollowUpTask(contactId = contactId, note = "Follow up with client"),
            TaskStatus.Pending,
            CreateTriggerRequest("scheduled", triggerTime)
        )

        // Act
        val savedTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, createTaskRequest)

        // Assert
        assertThat(savedTask.id).isNotNull
        assertThat(savedTask.tenantId).isEqualTo(tenantId)
        assertThat(savedTask.status).isEqualTo(TaskStatus.Pending)
        val followUpTask = savedTask.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Follow up with client")
    }

    @Test
    fun `create task without trigger successfully`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = java.util.UUID.randomUUID()
        val task = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "No trigger task"),
            status = TaskStatus.Pending
        )

        // Act
        val savedTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, task)

        // Assert
        assertThat(savedTask.id).isNotNull
        assertThat(savedTask.tenantId).isEqualTo(tenantId)
        assertThat(savedTask.status).isEqualTo(TaskStatus.Pending)
    }

    @Test
    fun `list pending and due tasks for user`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = java.util.UUID.randomUUID()

        // Create some tasks with different statuses
        val pendingTask = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Pending task"),
            status = TaskStatus.Pending
        )
        val dueTask = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Due task"),
            status = TaskStatus.Due
        )
        val completedTask = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Completed task"),
            status = TaskStatus.Completed
        )

        networkManager.createTaskWithTrigger(userId.toString(), tenantId, pendingTask)
        networkManager.createTaskWithTrigger(userId.toString(), tenantId, dueTask)
        networkManager.createTaskWithTrigger(userId.toString(), tenantId, completedTask)

        // Act
        val tasks = networkManager.listPendingAndDueTasks(userId.toString(), tenantId)

        // Assert
        assertThat(tasks).hasSize(2)
        assertThat(tasks).allSatisfy { task ->
            task.status == TaskStatus.Pending || task.status == TaskStatus.Due
        }
        val notes = tasks.map { (it.data as FollowUpTask).note }
        assertThat(notes).containsExactlyInAnyOrder("Pending task", "Due task")
    }

    @Test
    fun `triggerDueTriggers marks tasks as due and triggers as triggered`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = java.util.UUID.randomUUID()
        
        // Create tasks with triggers that are already past due
        val pastTime = java.time.Instant.now().minusSeconds(3600) // 1 hour ago
        val createTaskRequest1 = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Task 1"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", pastTime)
        )
        val createTaskRequest2 = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Task 2"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", pastTime)
        )
        
        val savedTask1 = networkManager.createTaskWithTrigger(userId.toString(), tenantId, createTaskRequest1)
        val savedTask2 = networkManager.createTaskWithTrigger(userId.toString(), tenantId, createTaskRequest2)

        // Act
        networkManager.triggerDueTriggers()

        // Assert
        val updatedTask1 = taskAccess.getTask(savedTask1.id!!)
        val updatedTask2 = taskAccess.getTask(savedTask2.id!!)
        
        assertThat(updatedTask1).isNotNull
        assertThat(updatedTask1!!.status).isEqualTo(TaskStatus.Due)
        assertThat(updatedTask2).isNotNull
        assertThat(updatedTask2!!.status).isEqualTo(TaskStatus.Due)
        
        val trigger1 = taskAccess.getTriggersByTaskId(savedTask1.id).first()
        val trigger2 = taskAccess.getTriggersByTaskId(savedTask2.id).first()
        
        assertThat(trigger1.status).isEqualTo(TriggerStatus.Triggered)
        assertThat(trigger2.status).isEqualTo(TriggerStatus.Triggered)
    }

    @Test
    fun `triggerDueTriggers does nothing when no due triggers exist`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = java.util.UUID.randomUUID()
        
        // Create task with future trigger
        val futureTime = java.time.Instant.now().plusSeconds(3600) // 1 hour from now
        val createTaskRequest = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Future task"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", futureTime)
        )
        
        val savedTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, createTaskRequest)

        // Act
        networkManager.triggerDueTriggers()

        // Assert - task should still be pending
        val updatedTask = taskAccess.getTask(savedTask.id!!)
        assertThat(updatedTask).isNotNull
        assertThat(updatedTask!!.status).isEqualTo(TaskStatus.Pending)
        
        val trigger = taskAccess.getTriggersByTaskId(savedTask.id).first()
        assertThat(trigger.status).isEqualTo(TriggerStatus.Pending)
    }

    @Test
    fun `triggerDueTriggers processes only pending triggers with past trigger time`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = java.util.UUID.randomUUID()
        
        // Create mix of tasks: past due, future, and already triggered
        val pastTime = java.time.Instant.now().minusSeconds(3600)
        val futureTime = java.time.Instant.now().plusSeconds(3600)
        
        val pastDueTask = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Past due task"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", pastTime)
        )
        val futureTask = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Future task"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", futureTime)
        )
        
        val savedPastDueTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, pastDueTask)
        val savedFutureTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, futureTask)

        // Act
        networkManager.triggerDueTriggers()

        // Assert
        val updatedPastDueTask = taskAccess.getTask(savedPastDueTask.id!!)
        val updatedFutureTask = taskAccess.getTask(savedFutureTask.id!!)
        
        assertThat(updatedPastDueTask!!.status).isEqualTo(TaskStatus.Due)
        assertThat(updatedFutureTask!!.status).isEqualTo(TaskStatus.Pending)
        
        val pastTrigger = taskAccess.getTriggersByTaskId(savedPastDueTask.id).first()
        val futureTrigger = taskAccess.getTriggersByTaskId(savedFutureTask.id).first()
        
        assertThat(pastTrigger.status).isEqualTo(TriggerStatus.Triggered)
        assertThat(futureTrigger.status).isEqualTo(TriggerStatus.Pending)
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