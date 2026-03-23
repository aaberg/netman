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
    val interactions: List<InteractionResource>
)

@Serdeable
data class ContactListItemResource(
    val id: UUID,
    val name: String,
    val initials: String = "",
    val title: String = "",
    val organization: String = "",
    val followUpStatus: ContactFollowUpStatus,
    val followUpIn: String = ""
)

@Serdeable
data class SaveContactRequest(
    val id: UUID? = null,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val title: String? = null,
    val organization: String? = null,
    val notes: String? = null
)

@Serdeable
data class ContactSavedResponse(
    val id: UUID
)

enum class ContactFollowUpStatus {
    Scheduled, Overdue, None
}

