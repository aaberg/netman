package netman.models

import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.util.UUID

@Introspected
data class Trigger(
    val id: UUID? = null,
    val triggerType: String,
    val triggerTime: Instant,
    val targetTaskId: UUID,
    val status: String,
    val statusTime: Instant
)
