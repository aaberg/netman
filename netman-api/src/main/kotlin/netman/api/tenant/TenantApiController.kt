package netman.api.tenant

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.MembershipManager
import netman.businesslogic.NetworkManager
import netman.businesslogic.models.MemberTenantResource
import netman.businesslogic.models.TenantSummaryResource


@Controller("/api/tenants", produces = ["application/json"])
@Secured(SecurityRule.IS_AUTHENTICATED)
class TenantApiController(
    private val membershipManager: MembershipManager,
    private val networkManager: NetworkManager
) : TenantApi {

    override fun getTenants(authentication: Authentication) : List<MemberTenantResource> {
        val user = getUserId(authentication)
        return membershipManager.getMemberTenants(user)
    }

    override fun getTenant(authentication: Authentication, tenantId: Long) : MemberTenantResource {
        val user = getUserId(authentication)
        return membershipManager.getMemberTenants(user).single { it.tenant.id == tenantId }
    }

    override fun getDefaultTenant(authentication: Authentication): MemberTenantResource {
        val user = getUserId(authentication)
        return membershipManager.getMemberDefaultTenant(user)
    }

    override fun getTenantSummary(authentication: Authentication, tenantId: Long): TenantSummaryResource {
        val user = getUserId(authentication)
        return networkManager.summariseTenant(user, tenantId)
    }
}