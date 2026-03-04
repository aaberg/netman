package netman.models

import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.*

@Serdeable
data class Communication(
    val id: UUID? = null,
    val contactId: UUID,
    val type: CommunicationType,
    val content: String,
    val timestamp: Instant,
    val metadata: Map<String, String> = emptyMap()
)

@Serdeable
enum class CommunicationType {
    EMAIL, CALL, TEXT_MESSAGE
}
