package netman.api.tenant.models

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class ContactResource(
    val id: Long,
    val name: String,
    val initials: String
)