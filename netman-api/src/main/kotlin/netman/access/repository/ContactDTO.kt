package netman.access.repository

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

/**
 * Data Transfer Object for Contact information
 */
@MappedEntity("contact")
data class ContactDTO(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.AUTO)
    val id: Long? = null,
    val tenantId: Long,
    val name: String,
)