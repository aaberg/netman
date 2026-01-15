package netman.access.repository

import io.micronaut.data.annotation.Embeddable
import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import java.io.Serializable

@Embeddable
data class LabelId(
    @field:MappedProperty("label")
    val label: String,
    @field:MappedProperty("tenant_id")
    val tenantId: Long
) : Serializable

@MappedEntity("view_labels")
data class LabelDTO(
    @field:EmbeddedId
    val id: LabelId
)
