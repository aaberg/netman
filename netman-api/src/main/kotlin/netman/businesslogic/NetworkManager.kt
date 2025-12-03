package netman.businesslogic

import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.TaskAccess
import netman.models.Contact2
import netman.models.Contact2ListItem
import netman.models.Task
import netman.models.TaskStatus
import netman.models.Trigger
import netman.models.TriggerStatus
import java.time.Instant
import java.util.UUID

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val taskAccess: TaskAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator,
    private val timeService: TimeService
) {

    fun getMyContacts(userId: String, tenantId: Long) : List<Contact2ListItem> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        return contactAccess.listContacts(tenantId)
    }

    fun getContactWithDetails(userId: String, tenantId: Long, contactId: UUID) : Contact2 {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return contactAccess.getContact(tenantId, contactId)
    }

    fun saveContactWithDetails(userId: String, tenantId: Long, contact: Contact2) : Contact2 {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val violations = validator.validate(contact )

        if (violations.isNotEmpty()){
            throw ValidationException(violations.toString())
        }

        val savedContact = contactAccess.saveContact(tenantId, contact)
        requireNotNull(savedContact.id)

        return savedContact
    }

    /**
     * Creates a follow-up task with an optional trigger.
     * The task is associated with the authenticated user.
     */
    fun createTaskWithTrigger(userId: String, task: Task, trigger: Trigger?): Task {
        val violations = validator.validate(task)
        if (violations.isNotEmpty()) {
            throw ValidationException(violations.toString())
        }

        // Save the task first
        val savedTask = taskAccess.saveTask(task)
        
        // If a trigger is provided, validate and save it
        trigger?.let {
            val triggerViolations = validator.validate(it)
            if (triggerViolations.isNotEmpty()) {
                throw ValidationException(triggerViolations.toString())
            }
            
            // Ensure trigger points to the saved task and set statusTime to current time
            val triggerWithTaskId = it.copy(
                targetTaskId = savedTask.id!!,
                statusTime = it.statusTime ?: timeService.now()
            )
            taskAccess.saveTrigger(triggerWithTaskId)
        }
        
        return savedTask
    }

    /**
     * Lists all pending and due tasks for a specific user.
     * Returns tasks with status Pending or Due.
     */
    fun listPendingAndDueTasks(userId: String): List<Task> {
        val userUuid = UUID.fromString(userId)
        val allTasks = taskAccess.getTasksByUserId(userUuid)
        
        return allTasks.filter { task ->
            task.status == TaskStatus.Pending || task.status == TaskStatus.Due
        }
    }

    /**
     * Lists all triggers that are pending and whose trigger time is due.
     * Returns triggers with status Pending where triggerTime is before the current time.
     */
    fun listPendingDueTriggers(): List<Trigger> {
        val currentTime = timeService.now()
        return taskAccess.getTriggersByStatusAndTime(TriggerStatus.Pending, currentTime)
    }
}