package netman.api.tenant

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.api.tenant.models.ContactResource
import netman.api.tenant.models.MemberTenantResource
import netman.api.tenant.models.TenantResourceMapper
import netman.businesslogic.MembershipManager
import netman.businesslogic.NetworkManager
import netman.businesslogic.models.ContactWithDetails


@Controller("/api/tenants", produces = ["application/json"])
@Secured(SecurityRule.IS_AUTHENTICATED)
class TenantApiController(
    private val membershipManager: MembershipManager,
    private val tenantResourceMapper: TenantResourceMapper,
    private val networkManager: NetworkManager
) : TenantApi {

    override fun getTenants(authentication: Authentication) : List<MemberTenantResource> {
        val user = getUserId(authentication)

        val tenants = membershipManager.getMemberTenants(user)
        val tenantResources = tenants.map { tenantResourceMapper.map(it) }
        return tenantResources
    }

    override fun getTenant(authentication: Authentication, tenantId: Long) : MemberTenantResource {
        val user = getUserId(authentication)

        val tenant = membershipManager.getMemberTenants(user).single { it.tenant.id == 1L }
        return tenantResourceMapper.map(tenant)
    }

    override fun getContactList(
        authentication: Authentication,
        tenantId: Long
    ): List<ContactResource> {
        val user = getUserId(authentication)
        val contacts = networkManager.getMyContacts(user, tenantId)
        val contactResources = contacts.map { tenantResourceMapper.map(it) }
        return contactResources
    }

    override fun createContactWithDetails(
        authentication: Authentication,
        tenantId: Long,
        contactWithDetails: ContactWithDetails
    ) {
        val user = getUserId(authentication)

    }

    override fun getContactDetails(
        authentication: Authentication,
        tenantId: Long,
        contactId: Long
    ): ContactWithDetails {
        TODO("Not yet implemented")
    }

    override fun updateContactWithDetails(
        authentication: Authentication,
        tenantId: Long,
        contactId: Long,
        contactWithDetails: ContactWithDetails
    ) {
        TODO("Not yet implemented")
    }
}