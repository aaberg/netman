package netman.api.businesslogic

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import netman.api.access.ProfileAccess
import netman.api.access.TenantAccess
import netman.api.models.Tenant
import netman.api.models.TenantType
import netman.api.models.UserProfile

@Singleton
open class MembershipManager(
    val tenantAccess: TenantAccess,
    val profileAccess: ProfileAccess) {

    @Transactional
    open fun registerUserWithPrivateTenant(userId: String, userFullName: String) : Tenant {
        val tenant = tenantAccess.registerNewTenant("Personal tenant", TenantType.PERSONAL, userId)
        profileAccess.storeProfile(userId, UserProfile(userFullName))

        return tenant
    }

    fun getProfile(userId: String) : UserProfile? = profileAccess.getProfile(userId)
}