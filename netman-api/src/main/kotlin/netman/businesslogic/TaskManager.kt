package netman.businesslogic

import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import netman.access.ActionAccess
import netman.access.ContactAccess
import netman.businesslogic.models.*
import netman.models.ActionStatus
import netman.models.COMMAND_TYPE_FOLLOWUP
import netman.models.CreateFollowUpCommand
import netman.models.Frequency
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * Manager for task-related operations including scheduled follow-ups and action processing.
 */
@Singleton
class TaskManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val contactResourceMapper: ContactResourceMapper,
    private val actionAccess: ActionAccess,
    private val timeService: TimeService
) {

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
        // Convert to zoned datetime to support adding weeks, months and years.
        val t = lastTriggerTime.atZone(ZoneId.of("UTC"))
        val resultTime = when (frequency) {
            Frequency.Weekly -> t.plusWeeks(1)
            Frequency.Biweekly -> t.plusWeeks(2)
            Frequency.Monthly -> t.plusMonths(1)
            Frequency.Quarterly -> t.plusMonths(3)
            Frequency.SemiAnnually -> t.plusMonths(6)
            Frequency.Annually -> t.plusYears(1)
            Frequency.Single -> t // Shouldn't happen, but handle gracefully
        }
        return resultTime.toInstant()
    }
}