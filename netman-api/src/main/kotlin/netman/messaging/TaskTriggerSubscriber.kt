package netman.messaging

import io.micronaut.nats.annotation.NatsListener
import io.micronaut.nats.annotation.Subject
import jakarta.inject.Singleton
import netman.businesslogic.TaskManager
import org.slf4j.LoggerFactory

/**
 * NATS subscriber for task trigger processing.
 * 
 * Listens to the "task.trigger.due" subject and triggers due task triggers.
 * Uses a queue group to ensure only one instance processes each message when multiple
 * application instances are running.
 */
@Singleton
@NatsListener
class TaskTriggerSubscriber(
    private val taskManager: TaskManager
) {
    
    private val log = LoggerFactory.getLogger(TaskTriggerSubscriber::class.java)

    /**
     * Handles messages on the "task.trigger.due" subject.
     * 
     * The queue group "task-trigger-processors" ensures that only one subscriber
     * in the group receives and processes each message, enabling load balancing
     * across multiple application instances.
     */
    @Subject(value = "task.trigger.due", queue = "task-trigger-processors")
    fun onTaskTriggerDue() {
        log.info("Received task.trigger.due message, processing due actions")
        try {
            taskManager.runPendingActions()
            log.info("Successfully processed due actions")
        } catch (e: Exception) {
            log.error("Error processing due actions", e)
            throw e
        }
    }
}
