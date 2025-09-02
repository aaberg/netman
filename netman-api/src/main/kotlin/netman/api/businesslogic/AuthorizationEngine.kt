package netman.api.businesslogic

import jakarta.inject.Singleton
import netman.api.access.TenantAccess

@Singleton
class AuthorizationEngine(val tenantAccess: TenantAccess) {

    fun validateAccessToTenantOrThrow(userId: String, tenantId: Long) {
        val memberTenants = tenantAccess.getMemberTenants(userId)

        if (memberTenants.none { mt -> mt.tenant.id == tenantId }) {
            throw ForbiddenException("User $userId does not have access to tenant $tenantId")
        }
    }
}