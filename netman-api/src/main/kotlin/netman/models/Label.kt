package netman.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class Label(
    val label: String,
    val tenantId: Long
)
