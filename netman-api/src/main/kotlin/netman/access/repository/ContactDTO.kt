package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant
import java.util.*

@MappedEntity("contact")
data class ContactDTO(
    @field:Id
    val id: UUID,
    val tenantId: Long,
    val lastUpdated: Instant,
    @field:TypeDef(type = DataType.JSON)
    val data: String
)
