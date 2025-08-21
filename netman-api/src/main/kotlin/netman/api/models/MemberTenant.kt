package netman.api.models

import java.lang.reflect.Member

data class MemberTenant(val tenant: Tenant, val userId: String, val role: TenantRole)
