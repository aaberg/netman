package netman.api.tenant.models

import io.micronaut.context.annotation.Mapper
import io.micronaut.serde.annotation.Serdeable
import netman.models.MemberTenant
import netman.models.Tenant

@Serdeable
data class TenantResource(
    val id: Long? = null,
    val name: String,
    val tenantType: String,
)

@Serdeable
data class MemberTenantResource(
    val tenant: TenantResource,
    val role: String
)

@Mapper
interface TenantResourceMapper {
    @Mapper
    fun mapTenant(tenant: Tenant): TenantResource

    fun map(memberTenant: MemberTenant) : MemberTenantResource =
        MemberTenantResource(
            tenant = mapTenant(memberTenant.tenant),
            role = memberTenant.role.toString()
        )
}
