package netman.models

import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.util.UUID

@Introspected
data class Task(
    val id: UUID? = null,
    val userId: UUID,
    val data: String,
    val status: String,
    val created: Instant? = null
)
