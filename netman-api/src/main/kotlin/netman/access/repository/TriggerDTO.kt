package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.Instant
import java.util.UUID

@MappedEntity("trigger")
data class TriggerDTO(
    @field:Id
    val id: UUID,
    val triggerType: String,
    val triggerTime: Instant,
    val targetTaskId: UUID,
    val status: String,
    val statusTime: Instant
)
