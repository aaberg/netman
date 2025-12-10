package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant
import java.util.UUID

@MappedEntity("task")
data class TaskDTO(
    @field:Id
    val id: UUID,
    val userId: UUID,
    val tenantId: Long,
    @field:TypeDef(type = DataType.JSON)
    val data: String,
    val status: String,
    val created: Instant
)
