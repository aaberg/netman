package netman.businesslogic

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ActionAccess
import netman.access.ContactAccess
import netman.access.TaskAccess
import netman.access.repository.LabelRepository
import netman.access.repository.toContactListItemDto
import netman.businesslogic.models.ActionScheduleResource
import netman.businesslogic.models.ContactListItemResource
import netman.businesslogic.models.ContactResource
import netman.businesslogic.models.ContactResourceMapper
import netman.businesslogic.models.CreateFollowUpTaskRequest
import netman.businesslogic.models.FollowUpActionResource
import netman.businesslogic.models.LabelResource
import netman.businesslogic.models.PageResource
import netman.businesslogic.models.PageableResource
import netman.businesslogic.models.TaskResource
import netman.businesslogic.models.TriggerResource
import netman.businesslogic.models.mapToFollowUpActionResource
import netman.models.*
import java.util.*

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val taskAccess: TaskAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator,
    private val timeService: TimeService,
    private val contactResourceMapper: ContactResourceMapper,
    private val labelRepository: LabelRepository,
    private val actionAccess: ActionAccess
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

    fun registerScheduledFollowUp(userId: String, tenantId: Long, contactId: UUID, note: String, schedule: ActionScheduleResource) : FollowUpActionResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val contact = contactAccess.getContact(tenantId, contactId)
        requireNotNull(contact.id)

        val action = actionAccess.registerNewAction(tenantId, CreateFollowUpCommand(contactId, note), schedule.triggerTime, schedule.frequency)
        val followUpActionResource = mapToFollowUpActionResource(tenantId, contactResourceMapper.map(contact), action, note)
        return followUpActionResource
    }

    fun getPendingFollowUps(userId: String, tenantId: Long, pageable: PageableResource): PageResource<FollowUpActionResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val actions = actionAccess.getActions(tenantId, ActionStatus.Pending, COMMAND_TYPE_FOLLOWUP,
            Pageable.from(pageable.page, pageable.pageSize))
        val allTenantContacts = contactAccess.listContacts(tenantId).associateBy { it.contactId }
        val followUpActionResources = actions.map { a ->
            val followUpCommand = a.command as CreateFollowUpCommand
            val contactResource = contactResourceMapper.map(allTenantContacts[followUpCommand.contactId])
            mapToFollowUpActionResource(tenantId, followUpCommand.contactId)
        }

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
     * Processes pending triggers whose trigger time has passed.
     * For each due trigger:
     * - Marks the corresponding task as Due
     * - Marks the trigger as Triggered
     */
    fun triggerDueTriggers() {
        val currentTime = timeService.now()
        val dueTriggers = taskAccess.getTriggersByStatusAndTime(TriggerStatus.Pending, currentTime)
        
        dueTriggers.forEach { trigger ->
            requireNotNull(trigger.targetTaskId)
            
            // Fetch and update the task to Due status
            val task = taskAccess.getTask(trigger.targetTaskId) ?: return@forEach
            
            val updatedTask = task.copy(status = TaskStatus.Due)
            taskAccess.saveTask(updatedTask)
            
            // Update trigger to Triggered status
            val updatedTrigger = trigger.copy(
                status = TriggerStatus.Triggered,
                statusTime = currentTime
            )
            taskAccess.saveTrigger(updatedTrigger)
        }
    }
    
    fun getLabels(userId: String, tenantId: Long): List<LabelResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return labelRepository.getLabels(tenantId)
            .sortedBy { it.label }
            .map { LabelResource(id = it.id, label = it.label, tenantId = it.tenantId) }
    }
}