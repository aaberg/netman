package netman.access

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import netman.models.FollowUpTask
import netman.models.Task
import netman.models.Trigger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskAccessTest : DefaultTestProperties() {

    @Inject
    lateinit var taskAccess: TaskAccess

    @Inject
    lateinit var membershipManager: MembershipManager

    @Inject
    lateinit var contactAccess: ContactAccess

    private fun createTestUser(): UUID {
        val userId = UUID.randomUUID().toString()
        membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return UUID.fromString(userId)
    }

    @Test
    fun `save new task and check that it gets assigned an ID`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task = Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Follow up on project proposal"),
            status = "pending"
        )

        // Act
        val savedTask = taskAccess.saveTask(task)

        // Assert
        assertThat(savedTask.id).isNotNull
        assertThat(savedTask.userId).isEqualTo(userId)
        assertThat(savedTask.data).isInstanceOf(FollowUpTask::class.java)
        val followUpTask = savedTask.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Follow up on project proposal")
        assertThat(savedTask.status).isEqualTo("pending")
        assertThat(savedTask.created).isNotNull
    }

    @Test
    fun `save task with explicit ID`() {
        // Arrange
        val userId = createTestUser()
        val taskId = UUID.randomUUID()
        val contactId = UUID.randomUUID()
        val task = Task(
            id = taskId,
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Initial consultation follow-up"),
            status = "active",
            created = Instant.now()
        )

        // Act
        val savedTask = taskAccess.saveTask(task)

        // Assert
        assertThat(savedTask.id).isEqualTo(taskId)
        assertThat(savedTask.userId).isEqualTo(userId)
        assertThat(savedTask.status).isEqualTo("active")
        val followUpTask = savedTask.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Initial consultation follow-up")
    }

    @Test
    fun `get task by ID`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task = Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Retrieve this task"),
            status = "pending"
        )
        val savedTask = taskAccess.saveTask(task)

        // Act
        val retrievedTask = taskAccess.getTask(savedTask.id!!)

        // Assert
        assertThat(retrievedTask).isNotNull
        assertThat(retrievedTask?.id).isEqualTo(savedTask.id)
        assertThat(retrievedTask?.userId).isEqualTo(userId)
        val followUpTask = retrievedTask?.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Retrieve this task")
        assertThat(retrievedTask?.status).isEqualTo("pending")
    }

    @Test
    fun `get task by ID returns null for non-existent task`() {
        // Arrange
        val nonExistentId = UUID.randomUUID()

        // Act
        val result = taskAccess.getTask(nonExistentId)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `get tasks by user ID`() {
        // Arrange
        val userId = createTestUser()
        val otherUserId = createTestUser()
        val contactId1 = UUID.randomUUID()
        val contactId2 = UUID.randomUUID()
        val contactId3 = UUID.randomUUID()
        
        val task1 = Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId1, note = "Task 1 note"),
            status = "pending"
        )
        val task2 = Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId2, note = "Task 2 note"),
            status = "active"
        )
        val task3 = Task(
            userId = otherUserId,
            data = FollowUpTask(contactId = contactId3, note = "Task 3 note"),
            status = "pending"
        )

        taskAccess.saveTask(task1)
        taskAccess.saveTask(task2)
        taskAccess.saveTask(task3)

        // Act
        val userTasks = taskAccess.getTasksByUserId(userId)

        // Assert
        assertThat(userTasks).hasSize(2)
        assertThat(userTasks).allSatisfy { it.userId == userId }
        val notes = userTasks.map { (it.data as FollowUpTask).note }
        assertThat(notes).containsExactlyInAnyOrder("Task 1 note", "Task 2 note")
    }

    @Test
    fun `update existing task`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task = Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Original note"),
            status = "pending"
        )
        val savedTask = taskAccess.saveTask(task)

        // Act
        val updatedTask = savedTask.copy(
            data = FollowUpTask(contactId = contactId, note = "Updated note"),
            status = "completed"
        )
        val result = taskAccess.saveTask(updatedTask)

        // Assert
        assertThat(result.id).isEqualTo(savedTask.id)
        val followUpTask = result.data as FollowUpTask
        assertThat(followUpTask.note).isEqualTo("Updated note")
        assertThat(result.status).isEqualTo("completed")

        // Verify the update persisted
        val retrievedTask = taskAccess.getTask(savedTask.id!!)
        assertThat(retrievedTask?.status).isEqualTo("completed")
        val retrievedFollowUp = retrievedTask?.data as FollowUpTask
        assertThat(retrievedFollowUp.note).isEqualTo("Updated note")
    }

    @Test
    fun `save new trigger and check that it gets assigned an ID`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Task for trigger"),
            status = "pending"
        ))

        val trigger = Trigger(
            triggerType = "scheduled",
            triggerTime = Instant.now().plusSeconds(3600),
            targetTaskId = task.id!!,
            status = "pending",
            statusTime = Instant.now()
        )

        // Act
        val savedTrigger = taskAccess.saveTrigger(trigger)

        // Assert
        assertThat(savedTrigger.id).isNotNull
        assertThat(savedTrigger.triggerType).isEqualTo("scheduled")
        assertThat(savedTrigger.targetTaskId).isEqualTo(task.id)
        assertThat(savedTrigger.status).isEqualTo("pending")
    }

    @Test
    fun `get trigger by ID`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Task"),
            status = "pending"
        ))

        val trigger = Trigger(
            triggerType = "event",
            triggerTime = Instant.now(),
            targetTaskId = task.id!!,
            status = "active",
            statusTime = Instant.now()
        )
        val savedTrigger = taskAccess.saveTrigger(trigger)

        // Act
        val retrievedTrigger = taskAccess.getTrigger(savedTrigger.id!!)

        // Assert
        assertThat(retrievedTrigger).isNotNull
        assertThat(retrievedTrigger?.id).isEqualTo(savedTrigger.id)
        assertThat(retrievedTrigger?.triggerType).isEqualTo("event")
        assertThat(retrievedTrigger?.targetTaskId).isEqualTo(task.id)
    }

    @Test
    fun `get trigger by ID returns null for non-existent trigger`() {
        // Arrange
        val nonExistentId = UUID.randomUUID()

        // Act
        val result = taskAccess.getTrigger(nonExistentId)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `get triggers by task ID`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task1 = taskAccess.saveTask(Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Task 1"),
            status = "pending"
        ))
        val task2 = taskAccess.saveTask(Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Task 2"),
            status = "pending"
        ))

        val trigger1 = Trigger(
            triggerType = "scheduled",
            triggerTime = Instant.now().plusSeconds(3600),
            targetTaskId = task1.id!!,
            status = "pending",
            statusTime = Instant.now()
        )
        val trigger2 = Trigger(
            triggerType = "event",
            triggerTime = Instant.now(),
            targetTaskId = task1.id!!,
            status = "active",
            statusTime = Instant.now()
        )
        val trigger3 = Trigger(
            triggerType = "manual",
            triggerTime = Instant.now(),
            targetTaskId = task2.id!!,
            status = "pending",
            statusTime = Instant.now()
        )

        taskAccess.saveTrigger(trigger1)
        taskAccess.saveTrigger(trigger2)
        taskAccess.saveTrigger(trigger3)

        // Act
        val task1Triggers = taskAccess.getTriggersByTaskId(task1.id!!)

        // Assert
        assertThat(task1Triggers).hasSize(2)
        assertThat(task1Triggers).allSatisfy { it.targetTaskId == task1.id }
        assertThat(task1Triggers.map { it.triggerType }).containsExactlyInAnyOrder(
            "scheduled",
            "event"
        )
    }

    @Test
    fun `update existing trigger`() {
        // Arrange
        val userId = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = userId,
            data = FollowUpTask(contactId = contactId, note = "Task"),
            status = "pending"
        ))

        val trigger = Trigger(
            triggerType = "scheduled",
            triggerTime = Instant.now(),
            targetTaskId = task.id!!,
            status = "pending",
            statusTime = Instant.now()
        )
        val savedTrigger = taskAccess.saveTrigger(trigger)

        // Act
        val updatedTrigger = savedTrigger.copy(
            status = "executed",
            statusTime = Instant.now()
        )
        val result = taskAccess.saveTrigger(updatedTrigger)

        // Assert
        assertThat(result.id).isEqualTo(savedTrigger.id)
        assertThat(result.status).isEqualTo("executed")

        // Verify the update persisted
        val retrievedTrigger = taskAccess.getTrigger(savedTrigger.id!!)
        assertThat(retrievedTrigger?.status).isEqualTo("executed")
    }
}
