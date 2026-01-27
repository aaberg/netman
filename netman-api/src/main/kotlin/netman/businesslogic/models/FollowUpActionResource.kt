package netman.businesslogic.models

import netman.models.Action
import netman.models.ActionStatus
import netman.models.Contact2
import netman.models.Frequency
import java.time.Instant
import java.util.*

data class FollowUpActionResource(
    val id: UUID,
    val tenantId: Long,
    val status: ActionStatus,
    val created: Instant,
    val triggerTime: Instant,
    val frequency: Frequency,
    val contact: ContactResource,
    val note: String?
)

fun mapToFollowUpActionResource(tenantId: Long, contact: ContactResource, action: Action, note: String?) : FollowUpActionResource {
    return FollowUpActionResource(
        action.id,
        tenantId,
        action.status,
        action.created,
        action.triggerTime,
        action.frequency,
        contact,
        note
    )
}