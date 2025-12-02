package netman.models

import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.util.UUID

enum class TriggerStatus {
    Pending,
    Triggered,
    Canceled
}

@Introspected
data class Trigger(
    val id: UUID? = null,
    val triggerType: String,
    val triggerTime: Instant,
    val targetTaskId: UUID,
    val status: TriggerStatus,
    val statusTime: Instant
)
