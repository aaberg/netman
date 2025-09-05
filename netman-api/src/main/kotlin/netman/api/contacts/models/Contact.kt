package netman.api.contacts.models

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class Contact(
    val id: Long,
    val name: String
)