package netman.api.contact

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Mapper
import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.annotation.Serdeable
import netman.models.CDetail
import netman.models.Contact
import netman.models.ContactDetail
import netman.models.ContactWithDetails

@Serdeable
data class ContactResource(
    val id: Long? = null,
    val name: String,
    val initials: String? = null
)

@Serdeable
data class ContactWithDetailsResource(
    val contact: ContactResource,
    val details: List<ContactDetailResource>
)

@Serdeable
data class ContactDetailResource(
    val id: Long? = null,
    val detail: CDetail
)

@Bean
abstract class ContactResourceMapper(
) {
    @Mapper
    abstract fun map(contact: Contact) : ContactResource

    @Mapper
    abstract fun map(contactResource: ContactResource) : Contact


    fun map(contactWithDetails: ContactWithDetails) : ContactWithDetailsResource {
        return ContactWithDetailsResource(
            contact = map(contactWithDetails.contact),
            details = contactWithDetails.details.map { detail ->
                ContactDetailResource(
                    detail.id,
                    detail.detail
                )
            }
        )
    }

    fun map(contactWithDetailsResource: ContactWithDetailsResource) : ContactWithDetails {
        return ContactWithDetails(
            contact = map(contactWithDetailsResource.contact),
            details = contactWithDetailsResource.details.map { detail ->
                ContactDetail<CDetail>(
                    detail.id,
                    detail.detail
                )
            }
        )
    }
}

