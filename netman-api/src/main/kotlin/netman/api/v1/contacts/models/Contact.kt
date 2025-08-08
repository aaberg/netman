package netman.api.v1.contacts.models

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class Contact(
    val id: Int,
    val name: String
)