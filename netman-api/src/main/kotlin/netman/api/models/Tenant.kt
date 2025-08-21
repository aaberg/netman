package netman.api.models

data class Tenant(
    val id: Long,
    val name: String,
    val tenantType: TenantType,
)
