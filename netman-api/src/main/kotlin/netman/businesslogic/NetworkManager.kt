package netman.businesslogic

import jakarta.inject.Singleton
import netman.access.ContactAccess
import netman.models.Contact

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine
) {

    fun getMyContacts(userId: String, tenantId: Long) : List<Contact> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        return contactAccess.getContacts(tenantId)
    }
}