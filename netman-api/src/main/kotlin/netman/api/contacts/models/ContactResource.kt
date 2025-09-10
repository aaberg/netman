package netman.api.contacts.models

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class ContactResource(
    val id: Long,
    val name: String
)