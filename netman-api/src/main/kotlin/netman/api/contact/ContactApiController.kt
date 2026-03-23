package netman.api.contact

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.NetworkManager
import netman.businesslogic.models.ContactDetailsResource
import netman.businesslogic.models.ContactListItemResource
import netman.businesslogic.models.ContactSavedResponse
import netman.businesslogic.models.SaveContactRequest
import java.util.*

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/tenants/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ContactApiController(
    private val networkManager: NetworkManager
) : ContactApi {
    override fun getContacts(
        authentication: Authentication,
        tenantId: Long
    ): List<ContactListItemResource> {
        val userId = getUserId(authentication)
        return networkManager.getMyContacts(userId, tenantId)
    }

    override fun getContactDetails(
        authentication: Authentication,
        tenantId: Long,
        contactId: UUID
    ): ContactDetailsResource {
        val userId = getUserId(authentication)
        return networkManager.getContactDetails(userId, tenantId, contactId)
    }

    override fun saveContact(
        authentication: Authentication,
        tenantId: Long,
        saveContactRequest: SaveContactRequest
    ) : ContactSavedResponse {
        val userId = getUserId(authentication)
        return networkManager.saveContact(userId, tenantId, saveContactRequest)
    }
}