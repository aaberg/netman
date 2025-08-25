package netman.api.managers

import io.micronaut.transaction.annotation.Transactional
import netman.api.access.TenantAccess

class MembershipManager(val tenantAccess: TenantAccess) {

    @Transactional
    fun registerUserWithPrivateTenant(userId: String) {
        throw NotImplementedError()
    }
}