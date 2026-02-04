package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.Frequency
import java.util.*

@Serdeable
data class RegisterScheduledFollowUpWithSpanRequest(
    val contactId: UUID,
    val note: String,
    val span: Int,
    val spanType: TimeSpanType,
    val frequency: Frequency,
)