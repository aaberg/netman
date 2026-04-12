package netman.models

import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.*

@Serdeable
data class Interaction(
    val id: UUID? = null,
    val contactId: UUID,
    val type: InteractionType,
    val content: String,
    val timestamp: Instant,
    val metadata: Map<String, String> = emptyMap()
)

@Serdeable
enum class InteractionType {
    EMAIL, CALL, TEXT_MESSAGE
}
