package netman.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class MemberTenant(
    val tenant: Tenant,
    val userId: String,
    val role: TenantRole
)
