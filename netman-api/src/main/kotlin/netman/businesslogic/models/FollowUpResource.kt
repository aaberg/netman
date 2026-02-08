package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

/**
 * Simplified follow-up resource for tenant summaries
 */
@Serdeable
data class FollowUpResource(
    val id: UUID,
    val contactId: UUID,
    val taskId: UUID,
    val note: String?,
    val created: Instant
)