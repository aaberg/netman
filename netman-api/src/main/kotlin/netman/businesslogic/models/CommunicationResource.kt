package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.CommunicationType
import java.time.Instant
import java.util.*

@Serdeable
data class CommunicationResource(
    val id: UUID? = null,
    val contactId: UUID,
    val type: CommunicationType,
    val content: String,
    val timestamp: Instant,
    val metadata: Map<String, String> = emptyMap()
)

@Serdeable
data class CommunicationWithContactResource(
    val communication: CommunicationResource,
    val contact: ContactResource
)
