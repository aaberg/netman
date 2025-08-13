package netman.api.access.repository

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity("tenant")
data class TenantDTO(
    @field:Id
    @field:GeneratedValue
    val id: Long? = null,
    val name: String,
    val type: String,
)
