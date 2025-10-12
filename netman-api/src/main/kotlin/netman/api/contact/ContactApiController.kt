package netman.api.contact

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.NetworkManager

@Controller("/api/tenants")
@Secured(SecurityRule.IS_AUTHENTICATED)
class ContactApiController(
    private val networkManager: NetworkManager,
    private val contactResourceMapper: ContactResourceMapper
) : ContactApi {
    override fun getContactList(
        authentication: Authentication,
        tenantId: Long
    ): List<ContactResource> {
        val user = getUserId(authentication)
        val contacts = networkManager.getMyContacts(user, tenantId)
        val contactResources = contacts.map { contactResourceMapper.map(it) }
        return contactResources
    }

    override fun createContactWithDetails(
        authentication: Authentication,
        tenantId: Long,
        contactWithDetailsRequest: ContactWithDetailsResource
    ) : ContactWithDetailsResource {
        val user = getUserId(authentication)

        val contactWDetail = contactResourceMapper.map(contactWithDetailsRequest)
        val savedContactWDetail = networkManager.saveContactWithDetails(tenantId, contactWDetail)

        return contactResourceMapper.map(savedContactWDetail)
    }

    override fun getContactDetails(
        authentication: Authentication,
        tenantId: Long,
        contactId: Long
    ): ContactWithDetailsResource {
        val user = getUserId(authentication)
        val contactWithDetails = networkManager.getContactWithDetails(user, tenantId, contactId)
        return contactResourceMapper.map(contactWithDetails)
    }

    override fun updateContactWithDetails(
        authentication: Authentication,
        tenantId: Long,
        contactId: Long,
        contactWithDetails: ContactWithDetailsResource
    ) {
        val user = getUserId(authentication)
        // Ensure the contact ID from the path is used if not present in payload
        val normalizedPayload = if (contactWithDetails.contact.id == null || contactWithDetails.contact.id != contactId) {
            contactWithDetails.copy(contact = contactWithDetails.contact.copy(id = contactId))
        } else contactWithDetails

        val domain = contactResourceMapper.map(normalizedPayload)
        networkManager.saveContactWithDetails(tenantId, domain)
    }
}