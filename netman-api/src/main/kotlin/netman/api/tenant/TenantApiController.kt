package netman.api.tenant

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.api.tenant.models.MemberTenantResource
import netman.api.tenant.models.TenantResourceMapper
import netman.businesslogic.MembershipManager


@Controller("/api/tenants", produces = ["application/json"])
@Secured(SecurityRule.IS_AUTHENTICATED)
class TenantApiController(
    private val membershipManager: MembershipManager,
    private val tenantResourceMapper: TenantResourceMapper,
) : TenantApi {

    override fun getTenants(authentication: Authentication) : List<MemberTenantResource> {
        val user = getUserId(authentication)

        val tenants = membershipManager.getMemberTenants(user)
        val tenantResources = tenants.map { tenantResourceMapper.map(it) }
        return tenantResources
    }

    override fun getTenant(authentication: Authentication, tenantId: Long) : MemberTenantResource {
        val user = getUserId(authentication)

        val tenant = membershipManager.getMemberTenants(user).single { it.tenant.id == tenantId }
        return tenantResourceMapper.map(tenant)
    }

    override fun getDefaultTenant(authentication: Authentication): MemberTenantResource {
        val user = getUserId(authentication)
        val tenant = membershipManager.getMemberDefaultTenant(user)
        return tenantResourceMapper.map(tenant)
    }


}