package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.CDetail
import java.util.*

@Serdeable
data class ContactDetailsResource(
    val id: UUID,
    val name: String,
    val initials: String? = "",
    val email: String?,
    val phone: String?,
    val title: String?,
    val organization: String?,
    val notes: String?,
    val interactions: List<InteractionResource>,
    val imageUrl: String? = null
)

@Serdeable
data class ContactListItemResource(
    val id: UUID,
    val name: String,
    val initials: String = "",
    val title: String = "",
    val organization: String = "",
    val followUpStatus: ContactFollowUpStatus,
    val followUpIn: String = "",
    val imageUrl: String? = null
)

@Serdeable
data class SaveContactRequest(
    val id: UUID? = null,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val title: String? = null,
    val organization: String? = null,
    val location: String? = null,
    val notes: String? = null,
    val tempFileId: String? = null,
    val tempFileMimeType: String? = null,
    val tempFileExtension: String? = null
)

@Serdeable
data class ContactSavedResponse(
    val id: UUID
)

@Serdeable
data class TemporaryImageUploadResponse(
    val tempFileId: String,
    val mimeType: String,
    val extension: String,
    val previewUrl: String,
    val previewUrlExpiresAt: java.time.Instant
)

enum class ContactFollowUpStatus {
    Scheduled, Overdue, None
}
