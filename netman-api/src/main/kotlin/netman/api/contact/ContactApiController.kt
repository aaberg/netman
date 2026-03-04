package netman.api.contact

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.NetworkManager
import netman.businesslogic.models.ContactListItemResource
import netman.businesslogic.models.ContactResource
import netman.businesslogic.models.CommunicationResource
import netman.businesslogic.models.CommunicationWithContactResource
import netman.businesslogic.models.LabelResource
import java.util.UUID

@Controller("/api/tenants")
@Secured(SecurityRule.IS_AUTHENTICATED)
class ContactApiController(
    private val networkManager: NetworkManager
) : ContactApi {
    override fun getContactList(
        authentication: Authentication,
        tenantId: Long
    ): List<ContactListItemResource> {
        val user = getUserId(authentication)
        return networkManager.getMyContacts(user, tenantId)
    }

    override fun saveContactWithDetails(
        authentication: Authentication,
        tenantId: Long,
        contactWithDetailsRequest: ContactResource
    ) : ContactResource {
        val userId = getUserId(authentication)
        return networkManager.saveContactWithDetails(userId, tenantId, contactWithDetailsRequest)
    }

    override fun getContactDetails(
        authentication: Authentication,
        tenantId: Long,
        contactId: UUID
    ): ContactResource {
        val user = getUserId(authentication)
        return networkManager.getContactWithDetails(user, tenantId, contactId)
    }
    
    override fun getLabels(
        authentication: Authentication,
        tenantId: Long
    ): List<LabelResource> {
        val user = getUserId(authentication)
        return networkManager.getLabels(user, tenantId)
    }
    
    override fun registerCommunication(
        authentication: Authentication,
        tenantId: Long,
        contactId: UUID,
        communication: CommunicationResource
    ): CommunicationResource {
        val userId = getUserId(authentication)
        // Ensure the contactId in the path matches the one in the body
        val communicationWithContactId = communication.copy(contactId = contactId)
        return networkManager.saveCommunication(userId, tenantId, communicationWithContactId)
    }
    
    override fun getCommunications(
        authentication: Authentication,
        tenantId: Long,
        contactId: UUID
    ): List<CommunicationWithContactResource> {
        val userId = getUserId(authentication)
        return networkManager.getCommunications(userId, tenantId, contactId)
    }
}