package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

/**
 * Represents a label resource.
 *
 * @property id The unique identifier of the label
 * @property label The label text
 * @property tenantId The tenant this label belongs to
 */
@Serdeable
data class LabelResource(
    val id: UUID,
    val label: String,
    val tenantId: Long
)
