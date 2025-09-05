package netman.access.repository

import io.micronaut.core.annotation.Introspected

@Introspected
data class TenantMemberDTO(
    val userId: String,
    val tenantId: Long,
    val role: String
)
