package netman.models

import io.micronaut.core.annotation.Introspected
import java.util.UUID

@Introspected
data class Label(
    val id: UUID,
    val label: String,
    val tenantId: Long
)
