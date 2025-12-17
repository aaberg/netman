package netman.api.tenant

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.api.tenant.models.MemberTenantResource
import netman.api.tenant.models.TenantResource
import netman.businesslogic.MembershipManager


@Controller("/api/tenants", produces = ["application/json"])
@Secured(SecurityRule.IS_AUTHENTICATED)
class TenantApiController(
    private val membershipManager: MembershipManager,
) : TenantApi {

    override fun getTenants(authentication: Authentication) : List<MemberTenantResource> {
        val user = getUserId(authentication)

        val tenants = membershipManager.getMemberTenants(user)
        val tenantResources = tenants.map { 
            MemberTenantResource(
                tenant = TenantResource(
                    id = it.tenant.id,
                    name = it.tenant.name,
                    tenantType = it.tenant.tenantType.toString()
                ),
                role = it.role.toString()
            )
        }
        return tenantResources
    }

    override fun getTenant(authentication: Authentication, tenantId: Long) : MemberTenantResource {
        val user = getUserId(authentication)

        val tenant = membershipManager.getMemberTenants(user).single { it.tenant.id == tenantId }
        return MemberTenantResource(
            tenant = TenantResource(
                id = tenant.tenant.id,
                name = tenant.tenant.name,
                tenantType = tenant.tenant.tenantType.toString()
            ),
            role = tenant.role.toString()
        )
    }

    override fun getDefaultTenant(authentication: Authentication): MemberTenantResource {
        val user = getUserId(authentication)
        val tenant = membershipManager.getMemberDefaultTenant(user)
        return MemberTenantResource(
            tenant = TenantResource(
                id = tenant.tenant.id,
                name = tenant.tenant.name,
                tenantType = tenant.tenant.tenantType.toString()
            ),
            role = tenant.role.toString()
        )
    }


}