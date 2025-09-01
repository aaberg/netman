package netman.api.managers

import io.micronaut.transaction.annotation.Transactional
import netman.api.access.TenantAccess
import netman.api.models.TenantType

class MembershipManager(val tenantAccess: TenantAccess) {

    @Transactional
    fun registerUserWithPrivateTenant(userId: String) {
        val tenant = tenantAccess.registerNewTenant("Personal tenant", TenantType.PERSONAL, userId)

    }
}