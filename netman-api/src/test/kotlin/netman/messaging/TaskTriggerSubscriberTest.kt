package netman.messaging

import io.micronaut.nats.annotation.NatsClient
import io.micronaut.nats.annotation.Subject
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.TenantAccess
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import netman.businesslogic.NetworkManager
import netman.businesslogic.models.CreateFollowUpTaskRequest
import netman.businesslogic.models.CreateTriggerRequest
import netman.models.FollowUpTask
import netman.models.TaskStatus
import netman.models.TriggerStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

/**
 * Test for TaskTriggerSubscriber.
 * 
 * Note: These tests require NATS to be running (typically via docker-compose).
 * If NATS is not available, the tests will fail with connection errors.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(startApplication = false)
class TaskTriggerSubscriberTest : DefaultTestProperties() {

    @Inject
    private lateinit var networkManager: NetworkManager

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Inject
    private lateinit var tenantAccess: TenantAccess

    @Inject
    private lateinit var taskAccess: netman.access.TaskAccess

    @Inject
    private lateinit var taskTriggerPublisher: TaskTriggerPublisher

    @Test
    fun `subscriber processes due triggers when message is published`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = UUID.randomUUID()

        // Create a task with a trigger that's already past due
        val pastTime = java.time.Instant.now().minusSeconds(3600) // 1 hour ago
        val createTaskRequest = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Test task"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", pastTime)
        )

        val savedTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, createTaskRequest)

        // Verify initial state
        val initialTask = taskAccess.getTask(savedTask.id!!)
        assertThat(initialTask!!.status).isEqualTo(TaskStatus.Pending)

        // Act - Publish message to trigger processing
        taskTriggerPublisher.publishTaskTriggerDue()

        // Give the subscriber time to process the message
        Thread.sleep(500)

        // Assert - Verify the task status was updated
        val updatedTask = taskAccess.getTask(savedTask.id)
        assertThat(updatedTask!!.status).isEqualTo(TaskStatus.Due)

        // Verify the trigger was marked as triggered
        val triggers = taskAccess.getTriggersByTaskId(savedTask.id)
        assertThat(triggers).hasSize(1)
        assertThat(triggers.first().status).isEqualTo(TriggerStatus.Triggered)
    }

    @Test
    fun `subscriber does not process future triggers`() {
        // Arrange
        val (userId, tenantId) = createTestUser()
        val contactId = UUID.randomUUID()

        // Create a task with a trigger in the future
        val futureTime = java.time.Instant.now().plusSeconds(3600) // 1 hour from now
        val createTaskRequest = CreateFollowUpTaskRequest(
            data = FollowUpTask(contactId = contactId, note = "Future task"),
            status = TaskStatus.Pending,
            trigger = CreateTriggerRequest("scheduled", futureTime)
        )

        val savedTask = networkManager.createTaskWithTrigger(userId.toString(), tenantId, createTaskRequest)

        // Act - Publish message to trigger processing
        taskTriggerPublisher.publishTaskTriggerDue()

        // Give the subscriber time to process the message
        Thread.sleep(500)

        // Assert - Verify the task status remains Pending
        val updatedTask = taskAccess.getTask(savedTask.id!!)
        assertThat(updatedTask!!.status).isEqualTo(TaskStatus.Pending)

        // Verify the trigger remains Pending
        val triggers = taskAccess.getTriggersByTaskId(savedTask.id)
        assertThat(triggers).hasSize(1)
        assertThat(triggers.first().status).isEqualTo(TriggerStatus.Pending)
    }

    private data class TestUserData(val userId: UUID, val tenantId: Long)

    private fun createTestUser(): TestUserData {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return TestUserData(UUID.fromString(userId), tenant.id)
    }

    /**
     * NATS client interface for publishing test messages.
     */
    @NatsClient
    interface TaskTriggerPublisher {
        @Subject("task.trigger.due")
        fun publishTaskTriggerDue()
    }
}
