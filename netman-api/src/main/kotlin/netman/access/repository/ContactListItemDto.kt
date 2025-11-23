package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import netman.models.Contact2
import netman.models.Email
import netman.models.Phone
import java.util.UUID

@MappedEntity("view_contact_list")
data class ContactListItemDto(
    @field:Id
    val contactId: UUID,
    val name: String,
    val tenantId: Long,
    val contactInfo: String,
    val contactInfoIcon: String,
    val labels: String,
    val hasUpdates: Boolean,
)

fun toContactListItemDto(contact: Contact2, tenantId: Long): ContactListItemDto {

    val contactInfo: String
    val contactInfoIcon: String

    val primaryEmail = contact.details
        .firstOrNull{c -> c is Email && c.isPrimary} as Email?

    val otherEmail = contact.details
        .filter { it is Email }
        .firstOrNull { it != primaryEmail } as Email?

    val fallbackPhone = contact.details
        .firstOrNull { it is Phone } as Phone?

    if (primaryEmail != null) {
        contactInfo = primaryEmail.address
        contactInfoIcon = "Email"
    } else if (otherEmail != null) {
        contactInfo = otherEmail.address
        contactInfoIcon = "Email"
    } else if (fallbackPhone != null) {
        contactInfo = fallbackPhone.number
        contactInfoIcon = "Phone"
    } else {
        contactInfo = ""
        contactInfoIcon = ""
    }

    return ContactListItemDto(
        contact.id!!,
        contact.name,
        tenantId,
        contactInfo,
        contactInfoIcon,
        "",
        false
    )
}