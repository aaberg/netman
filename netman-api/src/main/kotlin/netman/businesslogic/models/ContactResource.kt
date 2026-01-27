package netman.businesslogic.models

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Mapper
import io.micronaut.serde.annotation.Serdeable
import netman.models.CDetail
import netman.models.Contact2
import netman.models.Contact2ListItem
import netman.models.Email
import netman.models.Phone
import java.util.*

@Serdeable
data class ContactResource(
    val id: UUID? = null,
    val name: String,
    val initials: String? = "",
    val details: List<CDetail>
)

@Serdeable
data class ContactListItemResource(
    val id: UUID? = null,
    val name: String,
    val initials: String? = "",
    val contactInfo: String,
    val contactInfoIcon: String,
    val labels: String,
    val hasUpdates: Boolean
)

@Bean
abstract class ContactResourceMapper {
    @Mapper
    abstract fun map(contact: Contact2) : ContactResource

    @Mapper
    abstract fun map(contactResource: ContactResource) : Contact2

    @Mapper
    @Mapper.Mapping(to = "contactId", from = "id")
    abstract fun map(contactListItemResource: ContactListItemResource) : Contact2ListItem

    @Mapper
    @Mapper.Mapping(to = "id", from = "contactId")
    abstract fun map(contactListItem: Contact2ListItem) : ContactListItemResource

    fun mapToListItem(contact: Contact2) : ContactListItemResource {

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

        return ContactListItemResource(
            contact.id!!,
            contact.name,
            contact.initials,
            contactInfo,
            contactInfoIcon,
            "",
            false
        )
    }
}

