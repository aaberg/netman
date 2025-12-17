package netman.api.tenant.models

import io.micronaut.serde.annotation.Serdeable

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
