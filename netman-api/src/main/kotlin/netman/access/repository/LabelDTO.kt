package netman.access.repository

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.util.UUID

@MappedEntity("view_labels")
data class LabelDTO(
    @field:Id
    @field:GeneratedValue
    val id: UUID?,
    val label: String,
    val tenantId: Long
)
