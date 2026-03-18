package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant
import java.util.*

@MappedEntity("interaction")
data class InteractionDTO(
    @field:Id
    val id: UUID,
    val contactId: UUID,
    val type: String,
    val content: String,
    val timestamp: Instant,
    @field:TypeDef(type = DataType.JSON)
    val metadata: String?
)
