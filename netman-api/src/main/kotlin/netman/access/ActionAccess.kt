package netman.access

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import netman.access.repository.ActionDTO
import netman.access.repository.ActionRepository
import netman.access.repository.FollowUpRepository
import netman.businesslogic.TimeService
import netman.models.Action
import netman.models.ActionStatus
import netman.models.Command
import netman.models.Frequency
import java.time.Instant
import java.util.*

@Singleton
open class ActionAccess(
    private val actionRepository: ActionRepository,
    private val followupRepository: FollowUpRepository,
    private val timeService: TimeService,
    private val objectMapper: ObjectMapper
) {
    open fun registerNewAction(tenantId: Long, command: Command, triggerTime: Instant, frequency: Frequency): Action {
        val action = Action(
            UUID.randomUUID(),
            tenantId,
            ActionStatus.Pending,
            timeService.now(),
            triggerTime,
            frequency,
            command
        )

        val serializedCommand = objectMapper.writeValueAsString(command)

        val actionDto = ActionDTO(
            action.id,
            action.tenantId,
            action.status.toString(),
            action.created,
            action.triggerTime,
            action.frequency.toString(),
            serializedCommand
        )

        actionRepository.save(actionDto)

        return action
    }

    open fun getActions(tenantId: Long, status: ActionStatus, pageable: Pageable) : Page<Action> {
        val actionDtos = actionRepository.findByTenantIdAndStatus(tenantId, status.toString(), pageable)
        return actionDtos.map { a -> mapAction(a) }
    }
    
    open fun getAction(tenantId: Long, actionId: UUID): Action? {
        val actionDto = actionRepository.getById(actionId) ?: return null
        if (actionDto.tenantId != tenantId) {
            throw IllegalArgumentException("Action with ID $actionId does not belong to tenant $tenantId")
        }
        return mapAction(actionDto)
    }
    
    open fun updateActionStatus(action: Action, newStatus: ActionStatus) : Action {
        val actionDto = actionRepository.getById(action.id) ?: throw IllegalArgumentException("Action with ID ${action.id} does not exist")
        val updatedActionDto = actionRepository.update(actionDto.copy(status = newStatus.toString()))
        return mapAction(updatedActionDto)
    }

    private fun mapAction(actionDto: ActionDTO): Action {
        return Action(
            actionDto.id,
            actionDto.tenantId,
            ActionStatus.valueOf(actionDto.status),
            actionDto.created,
            actionDto.triggerTime,
            Frequency.valueOf(actionDto.frequency),
            objectMapper.readValue(actionDto.command, Command::class.java)
        )
    }
}