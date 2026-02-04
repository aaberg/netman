package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.Frequency
import java.util.*

/**
 * Unified request class for registering follow-up actions.
 * Supports both absolute time specification and relative time specification (span-based).
 */
@Serdeable
data class RegisterFollowUpRequest(
    val contactId: UUID,
    val note: String,
    val timeSpecification: FollowUpTimeSpecification,
    val frequency: Frequency,
)