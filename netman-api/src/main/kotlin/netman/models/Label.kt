package netman.models

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Introspected
@Serdeable
data class Label(
    val id: UUID,
    val label: String,
    val tenantId: Long
)
