package netman.access

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import netman.models.FollowUpTask
import netman.models.Task
import netman.models.TaskStatus
import netman.models.Trigger
import netman.models.TriggerStatus
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

    private data class TestUserData(val userId: UUID, val tenantId: Long)

    private fun createTestUser(): TestUserData {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return TestUserData(UUID.fromString(userId), tenant.id!!)
    }

    @Test
    fun `save new task and check that it gets assigned an ID`() {
        // Arrange
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Follow up on project proposal"),
            status = TaskStatus.Pending
        )

        // Act
        val savedTask = taskAccess.saveTask(task)

        // Assert
        assertThat(savedTask.id).isNotNull
        assertThat(savedTask.userId).isEqualTo(testUser.userId)
        assertThat(savedTask.tenantId).isEqualTo(testUser.tenantId)
        assertThat(savedTask.data).isInstanceOf(FollowUpTask::class.java)
        val followUpTask = savedTask.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Follow up on project proposal")
        assertThat(savedTask.status).isEqualTo(TaskStatus.Pending)
        assertThat(savedTask.created).isNotNull
    }

    @Test
    fun `save task with explicit ID`() {
        // Arrange
        val testUser = createTestUser()
        val taskId = UUID.randomUUID()
        val contactId = UUID.randomUUID()
        val task = Task(
            id = taskId,
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Initial consultation follow-up"),
            status = TaskStatus.Completed,
            created = Instant.now()
        )

        // Act
        val savedTask = taskAccess.saveTask(task)

        // Assert
        assertThat(savedTask.id).isEqualTo(taskId)
        assertThat(savedTask.userId).isEqualTo(testUser.userId)
        assertThat(savedTask.tenantId).isEqualTo(testUser.tenantId)
        assertThat(savedTask.status).isEqualTo(TaskStatus.Completed)
        val followUpTask = savedTask.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Initial consultation follow-up")
    }

    @Test
    fun `get task by ID`() {
        // Arrange
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Retrieve this task"),
            status = TaskStatus.Pending
        )
        val savedTask = taskAccess.saveTask(task)

        // Act
        val retrievedTask = taskAccess.getTask(savedTask.id!!)

        // Assert
        assertThat(retrievedTask).isNotNull
        assertThat(retrievedTask?.id).isEqualTo(savedTask.id)
        assertThat(retrievedTask?.userId).isEqualTo(testUser.userId)
        assertThat(retrievedTask?.tenantId).isEqualTo(testUser.tenantId)
        val followUpTask = retrievedTask?.data as FollowUpTask
        assertThat(followUpTask.contactId).isEqualTo(contactId)
        assertThat(followUpTask.note).isEqualTo("Retrieve this task")
        assertThat(retrievedTask?.status).isEqualTo(TaskStatus.Pending)
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
        val testUser1 = createTestUser()
        val testUser2 = createTestUser()
        val contactId1 = UUID.randomUUID()
        val contactId2 = UUID.randomUUID()
        val contactId3 = UUID.randomUUID()
        
        val task1 = Task(
            userId = testUser1.userId,
            tenantId = testUser1.tenantId,
            data = FollowUpTask(contactId = contactId1, note = "Task 1 note"),
            status = TaskStatus.Pending
        )
        val task2 = Task(
            userId = testUser1.userId,
            tenantId = testUser1.tenantId,
            data = FollowUpTask(contactId = contactId2, note = "Task 2 note"),
            status = TaskStatus.Completed
        )
        val task3 = Task(
            userId = testUser2.userId,
            tenantId = testUser2.tenantId,
            data = FollowUpTask(contactId = contactId3, note = "Task 3 note"),
            status = TaskStatus.Pending
        )

        taskAccess.saveTask(task1)
        taskAccess.saveTask(task2)
        taskAccess.saveTask(task3)

        // Act
        val userTasks = taskAccess.getTasksByUserId(testUser1.userId)

        // Assert
        assertThat(userTasks).hasSize(2)
        assertThat(userTasks).allSatisfy { it.userId == testUser1.userId }
        val notes = userTasks.map { (it.data as FollowUpTask).note }
        assertThat(notes).containsExactlyInAnyOrder("Task 1 note", "Task 2 note")
    }

    @Test
    fun `update existing task`() {
        // Arrange
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Original note"),
            status = TaskStatus.Pending
        )
        val savedTask = taskAccess.saveTask(task)

        // Act
        val updatedTask = savedTask.copy(
            data = FollowUpTask(contactId = contactId, note = "Updated note"),
            status = TaskStatus.Completed
        )
        val result = taskAccess.saveTask(updatedTask)

        // Assert
        assertThat(result.id).isEqualTo(savedTask.id)
        val followUpTask = result.data as FollowUpTask
        assertThat(followUpTask.note).isEqualTo("Updated note")
        assertThat(result.status).isEqualTo(TaskStatus.Completed)

        // Verify the update persisted
        val retrievedTask = taskAccess.getTask(savedTask.id!!)
        assertThat(retrievedTask?.status).isEqualTo(TaskStatus.Completed)
        val retrievedFollowUp = retrievedTask?.data as FollowUpTask
        assertThat(retrievedFollowUp.note).isEqualTo("Updated note")
    }

    @Test
    fun `save new trigger and check that it gets assigned an ID`() {
        // Arrange
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Task for trigger"),
            status = TaskStatus.Pending
        ))

        val trigger = Trigger(
            triggerType = "scheduled",
            triggerTime = Instant.now().plusSeconds(3600),
            targetTaskId = task.id!!,
            status = TriggerStatus.Pending,
            statusTime = Instant.now()
        )

        // Act
        val savedTrigger = taskAccess.saveTrigger(trigger)

        // Assert
        assertThat(savedTrigger.id).isNotNull
        assertThat(savedTrigger.triggerType).isEqualTo("scheduled")
        assertThat(savedTrigger.targetTaskId).isEqualTo(task.id)
        assertThat(savedTrigger.status).isEqualTo(TriggerStatus.Pending)
    }

    @Test
    fun `get trigger by ID`() {
        // Arrange
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Task"),
            status = TaskStatus.Pending
        ))

        val trigger = Trigger(
            triggerType = "event",
            triggerTime = Instant.now(),
            targetTaskId = task.id!!,
            status = TriggerStatus.Triggered,
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
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task1 = taskAccess.saveTask(Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Task 1"),
            status = TaskStatus.Pending
        ))
        val task2 = taskAccess.saveTask(Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Task 2"),
            status = TaskStatus.Pending
        ))

        val trigger1 = Trigger(
            triggerType = "scheduled",
            triggerTime = Instant.now().plusSeconds(3600),
            targetTaskId = task1.id!!,
            status = TriggerStatus.Pending,
            statusTime = Instant.now()
        )
        val trigger2 = Trigger(
            triggerType = "event",
            triggerTime = Instant.now(),
            targetTaskId = task1.id!!,
            status = TriggerStatus.Triggered,
            statusTime = Instant.now()
        )
        val trigger3 = Trigger(
            triggerType = "manual",
            triggerTime = Instant.now(),
            targetTaskId = task2.id!!,
            status = TriggerStatus.Pending,
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
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Task"),
            status = TaskStatus.Pending
        ))

        val trigger = Trigger(
            triggerType = "scheduled",
            triggerTime = Instant.now(),
            targetTaskId = task.id!!,
            status = TriggerStatus.Pending,
            statusTime = Instant.now()
        )
        val savedTrigger = taskAccess.saveTrigger(trigger)

        // Act
        val updatedTrigger = savedTrigger.copy(
            status = TriggerStatus.Triggered,
            statusTime = Instant.now()
        )
        val result = taskAccess.saveTrigger(updatedTrigger)

        // Assert
        assertThat(result.id).isEqualTo(savedTrigger.id)
        assertThat(result.status).isEqualTo(TriggerStatus.Triggered)

        // Verify the update persisted
        val retrievedTrigger = taskAccess.getTrigger(savedTrigger.id!!)
        assertThat(retrievedTrigger?.status).isEqualTo(TriggerStatus.Triggered)
    }

    @Test
    fun `get triggers by status and time`() {
        // Arrange
        val testUser = createTestUser()
        val contactId = UUID.randomUUID()
        val task = taskAccess.saveTask(Task(
            userId = testUser.userId,
            tenantId = testUser.tenantId,
            data = FollowUpTask(contactId = contactId, note = "Task"),
            status = TaskStatus.Pending
        ))

        val now = Instant.now()
        val pastTrigger1 = Trigger(
            triggerType = "scheduled",
            triggerTime = now.minusSeconds(3600),
            targetTaskId = task.id!!,
            status = TriggerStatus.Pending,
            statusTime = now
        )
        val pastTrigger2 = Trigger(
            triggerType = "event",
            triggerTime = now.minusSeconds(1800),
            targetTaskId = task.id,
            status = TriggerStatus.Pending,
            statusTime = now
        )
        val futureTrigger = Trigger(
            triggerType = "scheduled",
            triggerTime = now.plusSeconds(3600),
            targetTaskId = task.id,
            status = TriggerStatus.Pending,
            statusTime = now
        )
        val triggeredTrigger = Trigger(
            triggerType = "manual",
            triggerTime = now.minusSeconds(900),
            targetTaskId = task.id,
            status = TriggerStatus.Triggered,
            statusTime = now
        )

        taskAccess.saveTrigger(pastTrigger1)
        taskAccess.saveTrigger(pastTrigger2)
        taskAccess.saveTrigger(futureTrigger)
        taskAccess.saveTrigger(triggeredTrigger)

        // Act
        val pendingOldTriggers = taskAccess.getTriggersByStatusAndTime(TriggerStatus.Pending, now)

        // Assert
        assertThat(pendingOldTriggers).hasSize(2)
        assertThat(pendingOldTriggers).allSatisfy { trigger ->
            trigger.status == TriggerStatus.Pending && trigger.triggerTime.isBefore(now) 
        }
        assertThat(pendingOldTriggers.map { it.triggerType }).containsExactlyInAnyOrder(
            "scheduled",
            "event"
        )
    }
}
