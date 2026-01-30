package netman.businesslogic

import io.micronaut.data.model.Pageable
import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ActionAccess
import netman.access.ContactAccess
import netman.access.repository.LabelRepository
import netman.businesslogic.models.*
import netman.models.*
import java.util.*
import java.time.Instant

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
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

        return contacts.map { contactResourceMapper.mapToListItem(it) }
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

    fun registerScheduledFollowUp(userId: String, tenantId: Long, req: RegisterScheduledFollowUpRequest) : FollowUpActionResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val contact = contactAccess.getContact(tenantId, req.contactId)
        requireNotNull(contact.id)

        val action = actionAccess.registerNewAction(tenantId, CreateFollowUpCommand(req.contactId, req.note), req.triggerTime, req.frequency)
        val followUpActionResource = mapToFollowUpActionResource(tenantId, contactResourceMapper.map(contact), action, req.note)
        return followUpActionResource
    }

    fun getPendingFollowUps(userId: String, tenantId: Long, pageable: PageableResource): PageResource<FollowUpActionResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val actions = actionAccess.getActions(tenantId, ActionStatus.Pending, COMMAND_TYPE_FOLLOWUP,
            Pageable.from(pageable.page, pageable.pageSize))
        val allTenantContacts = contactAccess.listContacts(tenantId).associateBy { it.id }
        val followUpActionResources = actions.map { a ->
            val followUpCommand = a.command as CreateFollowUpCommand
            val followUpContact = allTenantContacts[followUpCommand.contactId]
            if (followUpContact == null) {
                // TODO if a contact is deleted, the corresponding follow-up action should be removed instead of throwing
                //  exception.
                throw IllegalStateException("Contact with ID ${followUpCommand.contactId} not found in tenant $tenantId")
            }
            val contactResource = contactResourceMapper.map(followUpContact)
            mapToFollowUpActionResource(tenantId, contactResource, a, followUpCommand.note)
        }

        return PageResource(actions.pageNumber, actions.size, actions.totalPages, followUpActionResources.content)
    }

    /**
     * Creates a follow-up task with an optional trigger.
     * The task is associated with the authenticated user and a specific tenant.
     */

    /**
     * Processes pending actions for a specific tenant whose trigger time has passed.
     * For each overdue action:
     * - If it's a FollowUpAction, registers a follow-up using ActionAccess
     * - For recurring actions, creates a new action scheduled for the next occurrence
     * - Marks the action as Completed
     * 
     * @param tenantId The tenant ID to process actions for
     */
    fun runPendingActions() {
        val currentTime = timeService.now()
        val pendingActions = actionAccess.getAllDueActions()
        
        pendingActions.forEach { action ->
            // Check if action is overdue (triggerTime is in the past)
            if (action.triggerTime.isBefore(currentTime)) {
                when (action.type) {
                    COMMAND_TYPE_FOLLOWUP -> {
                        val followUpCommand = action.command as CreateFollowUpCommand
                        
                        // Register the follow-up
                        actionAccess.registerFollowUp(
                            tenantId = action.tenantId,
                            contactId = followUpCommand.contactId,
                            taskId = UUID.randomUUID(), // Using random UUID for taskId as no task is created
                            note = followUpCommand.note
                        )
                    }
                    // Add other action types here if needed
                }
                
                // Mark the action as completed
                actionAccess.updateActionStatus(action, ActionStatus.Completed)
                // For recurring actions, create a new action for the next occurrence
                if (action.frequency != Frequency.Single) {
                    val nextTriggerTime = calculateNextTriggerTime(action.triggerTime, action.frequency, currentTime)
                    actionAccess.registerNewAction(
                        tenantId = action.tenantId,
                        command = action.command,
                        triggerTime = nextTriggerTime,
                        frequency = action.frequency
                    )
                }
            }
        }
    }

    /**
     * Calculates the next trigger time for a recurring action based on its frequency.
     * 
     * @param lastTriggerTime The original trigger time of the action
     * @param frequency The frequency of the recurring action
     * @param currentTime The current time (used to ensure we don't schedule in the past)
     * @return The next trigger time based on the frequency
     */
    private fun calculateNextTriggerTime(lastTriggerTime: Instant, frequency: Frequency, currentTime: Instant): Instant {
        return when (frequency) {
            Frequency.Weekly -> lastTriggerTime.plusSeconds(7 * 24 * 60 * 60)
            Frequency.Biweekly -> lastTriggerTime.plusSeconds(14 * 24 * 60 * 60)
            Frequency.Monthly -> lastTriggerTime.plusSeconds(30 * 24 * 60 * 60)
            Frequency.Quarterly -> lastTriggerTime.plusSeconds(90 * 24 * 60 * 60)
            Frequency.SemiAnnually -> lastTriggerTime.plusSeconds(180 * 24 * 60 * 60)
            Frequency.Annually -> lastTriggerTime.plusSeconds(365 * 24 * 60 * 60)
            Frequency.Single -> currentTime // Shouldn't happen, but handle gracefully
        }
    }
    
    fun getLabels(userId: String, tenantId: Long): List<LabelResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return labelRepository.getLabels(tenantId)
            .sortedBy { it.label }
            .map { LabelResource(id = it.id, label = it.label, tenantId = it.tenantId) }
    }
}