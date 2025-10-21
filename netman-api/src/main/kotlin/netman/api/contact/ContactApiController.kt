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

    override fun saveContactWithDetails(
        authentication: Authentication,
        tenantId: Long,
        contactWithDetailsRequest: ContactWithDetailsResource
    ) : ContactWithDetailsResource {
        val userId = getUserId(authentication)

        val contactWDetail = contactResourceMapper.map(contactWithDetailsRequest)
        val savedContactWDetail = networkManager.saveContactWithDetails(userId, tenantId, contactWDetail)

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
}