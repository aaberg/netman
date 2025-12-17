package netman.businesslogic

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import netman.access.ProfileAccess
import netman.access.TenantAccess
import netman.businesslogic.helper.InitialsGenerator
import netman.businesslogic.models.MemberTenantResource
import netman.businesslogic.models.ProfileResource
import netman.businesslogic.models.TenantResource
import netman.models.TenantType
import netman.models.UserProfile

@Singleton
open class MembershipManager(
    val tenantAccess: TenantAccess,
    val profileAccess: ProfileAccess) {

    @Transactional
    open fun registerUserWithPrivateTenant(userId: String, userFullName: String) : TenantResource {
        val userIdUUID = java.util.UUID.fromString(userId)
        val existing = tenantAccess.getMemberTenants(userId)
            .firstOrNull { it.tenant.tenantType == TenantType.PERSONAL }
            ?.tenant
        val tenant = existing ?: tenantAccess.registerNewTenant("Personal tenant", TenantType.PERSONAL, userId)
        profileAccess.storeProfile(userIdUUID, UserProfile(userFullName, InitialsGenerator.generateInitials(userFullName)))
        return TenantResource(tenant.id, tenant.name, tenant.tenantType)
    }

    fun getMemberTenants(userId: String) : List<MemberTenantResource> {
        val memberTenants = tenantAccess.getMemberTenants(userId)
        return memberTenants.map { memberTenant ->
            MemberTenantResource(
                tenant = TenantResource(
                    id = memberTenant.tenant.id,
                    name = memberTenant.tenant.name,
                    tenantType = memberTenant.tenant.tenantType
                ),
                userId = memberTenant.userId,
                role = memberTenant.role
            )
        }
    }

    fun getMemberDefaultTenant(userId: String) : MemberTenantResource {
        val tenants = tenantAccess.getMemberTenants(userId)
        val memberTenant = tenants.single{tenant -> tenant.tenant.tenantType == TenantType.PERSONAL}
        return MemberTenantResource(
            tenant = TenantResource(
                id = memberTenant.tenant.id,
                name = memberTenant.tenant.name,
                tenantType = memberTenant.tenant.tenantType
            ),
            userId = memberTenant.userId,
            role = memberTenant.role
        )
    }

    fun getProfile(userId: String) : ProfileResource?{
        val userIdUUID = java.util.UUID.fromString(userId)
        val profile = profileAccess.getProfile(userIdUUID)
        return profile?.let { ProfileResource(it.name, it.initials) }
    }
}