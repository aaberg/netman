package netman.access

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import netman.access.repository.ActionDTO
import netman.access.repository.ActionRepository
import netman.access.repository.FollowUpDTO
import netman.access.repository.FollowUpRepository
import netman.businesslogic.TimeService
import netman.models.Action
import netman.models.ActionStatus
import netman.models.Command
import netman.models.Frequency
import netman.models.FollowUp
import netman.models.FollowUpStatus
import netman.models.getCommandType
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
        val actionId = UUID.randomUUID()
        val serializedCommand = objectMapper.writeValueAsString(command)
        val commandType = getCommandType(command)

        val actionDto = ActionDTO(
            actionId,
            tenantId,
            commandType,
            ActionStatus.Pending.toString(),
            timeService.now(),
            triggerTime,
            frequency.toString(),
            serializedCommand
        )

        val savedAction = actionRepository.save(actionDto)

        return mapAction(savedAction)
    }

    open fun getActions(tenantId: Long, status: ActionStatus, type: String?, pageable: Pageable) : Page<Action> {
        val actionDtos: Page<ActionDTO>

        if (type != null) {
            actionDtos = actionRepository.findByTenantIdAndStatusAndType(tenantId, status.toString(), type, pageable)
        } else {
            actionDtos = actionRepository.findByTenantIdAndStatus(tenantId, status.toString(), pageable)
        }
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

    open fun registerFollowUp(tenantId: Long, contactId: UUID, taskId: UUID, note: String?): FollowUp {
        val followUpid = UUID.randomUUID()

        val followUpDto = FollowUpDTO(
            followUpid,
            tenantId,
            contactId,
            taskId,
            FollowUpStatus.Pending.toString(),
            timeService.now(),
            note
        )

        val savedFollowUp = followupRepository.save(followUpDto)

        return mapFollowUp(savedFollowUp)
    }

    open fun getFollowUps(tenantId: Long, status: FollowUpStatus, pageable: Pageable): Page<FollowUp> {
        val followUpDtos = followupRepository.findByTenantIdAndStatus(tenantId, status.toString(), pageable)
        return followUpDtos.map { f -> mapFollowUp(f) }
    }

    private fun mapAction(actionDto: ActionDTO): Action {
        return Action(
            actionDto.id,
            actionDto.tenantId,
            actionDto.type,
            ActionStatus.valueOf(actionDto.status),
            actionDto.created,
            actionDto.triggerTime,
            Frequency.valueOf(actionDto.frequency),
            objectMapper.readValue(actionDto.command, Command::class.java)
        )
    }

    private fun mapFollowUp(followUpDto: FollowUpDTO): FollowUp {
        return FollowUp(
            followUpDto.id,
            followUpDto.tenantId,
            followUpDto.contactId,
            followUpDto.taskId,
            FollowUpStatus.valueOf(followUpDto.status),
            followUpDto.created,
            followUpDto.note
        )
    }
}