package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import java.time.Instant
import java.util.UUID

@MappedEntity("follow_up")
data class FollowUpDTO(
    @field:Id
    val id: UUID,
    val tenantId: Long,
    @field:MappedProperty("contact_id")
    val contactId: UUID,
    @field:MappedProperty("task_id")
    val taskId: UUID,
    val status: String,
    val created: Instant,
    val note: String?
)
