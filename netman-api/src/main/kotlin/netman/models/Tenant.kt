package netman.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class Tenant(
    val id: Long,
    val name: String,
    val tenantType: TenantType,
)
