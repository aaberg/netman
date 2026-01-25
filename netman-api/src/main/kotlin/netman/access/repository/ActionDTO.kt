package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant
import java.util.UUID

@MappedEntity("action")
data class ActionDTO(
    @field:Id
    val id: UUID,
    val tenantId: Long,
    val status: String,
    val created: Instant,
    @field:MappedProperty("trigger_time")
    val triggerTime: Instant,
    val frequency: String,
    @field:TypeDef(type = DataType.JSON)
    val command: String
)
