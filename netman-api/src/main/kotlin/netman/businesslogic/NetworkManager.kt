package netman.businesslogic

import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.TaskAccess
import netman.api.contact.ContactListItemResource
import netman.api.contact.ContactResource
import netman.api.contact.ContactResourceMapper
import netman.businesslogic.models.CreateFollowUpTaskRequest
import netman.businesslogic.models.TaskResource
import netman.businesslogic.models.TriggerResource
import netman.models.*
import java.util.*

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val taskAccess: TaskAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator,
    private val timeService: TimeService,
    private val contactResourceMapper: ContactResourceMapper
) {

    fun getMyContacts(userId: String, tenantId: Long): List<ContactListItemResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val contacts = contactAccess.listContacts(tenantId)
        return contacts.map { contactResourceMapper.map(it) }
    }

    fun getContactWithDetails(userId: String, tenantId: Long, contactId: UUID): ContactResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val contact = contactAccess.getContact(tenantId, contactId)
        return contactResourceMapper.map(contact)
    }

    fun saveContactWithDetails(userId: String, tenantId: Long, contactResource: ContactResource): ContactResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        
        val contact = contactResourceMapper.map(contactResource)
        val violations = validator.validate(contact)

        if (violations.isNotEmpty()) {
            throw ValidationException(violations.toString())
        }

        val savedContact = contactAccess.saveContact(tenantId, contact)
        requireNotNull(savedContact.id)

        return contactResourceMapper.map(savedContact)
    }

    /**
     * Creates a follow-up task with an optional trigger.
     * The task is associated with the authenticated user and a specific tenant.
     */
    fun createTaskWithTrigger(
        userId: String,
        tenantId: Long,
        createTaskRequest: CreateFollowUpTaskRequest
    ): TaskResource {
        val userIdUUID = UUID.fromString(userId)

        // Validate user has access to the tenant
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val violations = validator.validate(createTaskRequest)
        if (violations.isNotEmpty()) {
            throw ValidationException(violations.toString())
        }

        // Save the task first
        val task = Task(
            userId = userIdUUID,
            tenantId = tenantId,
            data = createTaskRequest.data,
            status = createTaskRequest.status
        )
        val savedTask = taskAccess.saveTask(task)

        // If a trigger is provided, validate and save it
        val savedTrigger = createTaskRequest.trigger?.let {
            val triggerViolations = validator.validate(it)
            if (triggerViolations.isNotEmpty()) {
                throw ValidationException(triggerViolations.toString())
            }

            // Ensure trigger points to the saved task and set statusTime to current time
            val trigger = Trigger(
                triggerType = "FollowUp",
                triggerTime = it.triggerTime,
                targetTaskId = savedTask.id,
                status = TriggerStatus.Pending,
                statusTime = timeService.now()
            )
            taskAccess.saveTrigger(trigger)
        }

        return TaskResource(
            id = savedTask.id,
            tenantId = tenantId,
            data = savedTask.data,
            status = savedTask.status,
            created = savedTask.created,
            triggers = if (savedTrigger != null)
                listOf(
                    TriggerResource(
                        id = savedTrigger.id,
                        triggerType = savedTrigger.triggerType,
                        triggerTime = savedTrigger.triggerTime,
                        targetTaskId = savedTrigger.targetTaskId,
                        status = savedTrigger.status,
                        statusTime = savedTrigger.statusTime
                    )
                ) else emptyList()

        )
    }

    /**
     * Lists all pending and due tasks for a specific user and tenant.
     * Validates that the user has access to the tenant.
     * Returns tasks with status Pending or Due.
     */
    fun listPendingAndDueTasks(userId: String, tenantId: Long): List<TaskResource> {
        val userIdUUID = UUID.fromString(userId)

        // Validate user has access to the specific tenant
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val allTasks = taskAccess.getTasksByUserIdAndTenantId(userIdUUID, tenantId)


        return allTasks
            .filter { it.status == TaskStatus.Pending || it.status == TaskStatus.Due }
            .map { task ->
                requireNotNull(task.id)
                val triggers = taskAccess.getTriggersByTaskId(task.id)
                TaskResource(task.id, tenantId, task.data, task.status, task.created, triggers.map {trigger ->
                    TriggerResource(
                        id = trigger.id,
                        triggerType = trigger.triggerType,
                        triggerTime = trigger.triggerTime,
                        targetTaskId = trigger.targetTaskId,
                        status = trigger.status,
                        statusTime = trigger.statusTime
                    )
                })
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