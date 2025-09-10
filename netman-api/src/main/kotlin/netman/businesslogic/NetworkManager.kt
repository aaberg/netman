package netman.businesslogic

import jakarta.inject.Singleton
import netman.access.ContactAccess
import netman.api.contacts.models.ContactResource

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine
) {

    fun getMyContacts(userId: String, tenantId: Long) : List<ContactResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        return contactAccess.getContacts(tenantId)
    }
}