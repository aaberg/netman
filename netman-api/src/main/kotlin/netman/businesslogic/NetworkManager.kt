package netman.businesslogic

import jakarta.inject.Singleton
import netman.access.ContactAccess
import netman.models.Contact
import netman.models.ContactWithDetails

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine,
) {

    fun getMyContacts(userId: String, tenantId: Long) : List<Contact> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        return contactAccess.getContacts(tenantId)
    }

    fun getContactWithDetails(userId: String, tenantId: Long, contactId: Long) : ContactWithDetails {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val contact = contactAccess.getContact(contactId) ?: throw IllegalArgumentException("Contact with id $contactId not found")
        val details = contactAccess.getContactDetails(contactId)
        return ContactWithDetails(contact, details)
    }

    fun saveContactWithDetails(tenantId: Long, contactWithDetails: ContactWithDetails) : ContactWithDetails {
        val contact = contactAccess.saveContact(tenantId, contactWithDetails.contact)
        if (contact.id == null) {
            throw IllegalArgumentException("Contact could not be saved. Returned contact has no id")
        }
        val contactDetails = contactAccess.saveDetails(contact.id, contactWithDetails.details)

        return ContactWithDetails(contact, contactDetails)
    }
}