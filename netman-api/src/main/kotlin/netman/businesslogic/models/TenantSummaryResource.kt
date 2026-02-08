package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

/**
 * Summary resource containing key metrics and data for a tenant
 */
@Serdeable
data class TenantSummaryResource(
    val tenantId: Long,
    val numberOfContacts: Int,
    val numberOfPendingActions: Int,
    val pendingFollowUps: List<FollowUpResource>
)