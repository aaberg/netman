package netman.models

import java.time.Instant
import java.util.UUID

data class FollowUp(
    val id: UUID,
    val tenantId: Long,
    val contactId: UUID,
    val taskId: UUID,
    val status: FollowUpStatus,
    val created: Instant,
    val note: String?
)

enum class FollowUpStatus {
    Pending, Done
}
