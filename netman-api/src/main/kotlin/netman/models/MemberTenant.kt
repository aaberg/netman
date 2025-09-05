package netman.models

data class MemberTenant(val tenant: Tenant, val userId: String, val role: TenantRole)
