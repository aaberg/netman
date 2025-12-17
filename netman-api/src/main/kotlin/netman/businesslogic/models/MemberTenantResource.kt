package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.TenantRole

/**
 * Represents a member's relationship to a tenant.
 *
 * @property tenant The tenant information
 * @property userId The user ID of the member
 * @property role The role of the member in the tenant
 */
@Serdeable
data class MemberTenantResource(
    val tenant: TenantResource,
    val userId: String,
    val role: TenantRole
)
