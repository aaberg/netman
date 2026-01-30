package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.Frequency
import java.time.Instant
import java.util.*

@Serdeable
data class RegisterScheduledFollowUpRequest(
    val contactId: UUID,
    val note: String,
    val triggerTime: Instant,
    val frequency: Frequency,
)
