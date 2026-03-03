package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.Action
import netman.models.ActionStatus
import netman.models.Command
import netman.models.Frequency
import java.time.Instant
import java.util.*

@Serdeable
data class ActionResource(
    val id: UUID,
    val tenantId: Long,
    val type: String,
    val status: ActionStatus,
    val created: Instant,
    val triggerTime: Instant,
    val frequency: Frequency,
    val command: Command
)

fun mapToActionResource(action: Action): ActionResource {
    return ActionResource(
        action.id,
        action.tenantId,
        action.type,
        action.status,
        action.created,
        action.triggerTime,
        action.frequency,
        action.command
    )
}
