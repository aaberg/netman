package netman.businesslogic

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import netman.access.ProfileAccess
import netman.access.TenantAccess
import netman.businesslogic.helper.InitialsGenerator
import netman.models.Tenant
import netman.models.TenantType
import netman.models.UserProfile

@Singleton
open class MembershipManager(
    val tenantAccess: TenantAccess,
    val profileAccess: ProfileAccess) {

    @Transactional
    open fun registerUserWithPrivateTenant(userId: String, userFullName: String) : Tenant {
        val tenant = tenantAccess.registerNewTenant("Personal tenant", TenantType.PERSONAL, userId)
        profileAccess.storeProfile(userId, UserProfile(userFullName, InitialsGenerator.generateInitials(userFullName)))

        return tenant
    }

    fun getProfile(userId: String) : UserProfile? = profileAccess.getProfile(userId)
}