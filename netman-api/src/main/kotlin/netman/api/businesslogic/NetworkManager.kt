package netman.api.businesslogic

import jakarta.inject.Singleton
import netman.api.access.TenantAccess
import netman.api.v1.contacts.models.Contact

@Singleton
class NetworkManager(
    private val tenantAccess: TenantAccess,
    private val authorizationEngine: AuthorizationEngine
) {

    fun getMyContacts(userId: String, tenantId: Long) : List<Contact> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        return tenantAccess.getContacts(tenantId)
    }
}