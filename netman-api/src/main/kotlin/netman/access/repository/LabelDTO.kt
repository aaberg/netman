package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity("view_labels")
data class LabelDTO(
    @field:Id
    val label: String,
    val tenantId: Long
)
