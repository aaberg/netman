package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.TenantType

/**
 * Represents a tenant resource.
 *
 * @property id The unique identifier of the tenant
 * @property name The name of the tenant
 * @property tenantType The type of the tenant
 */
@Serdeable
data class TenantResource(
    val id: Long,
    val name: String,
    val tenantType: TenantType
)
