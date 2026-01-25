package netman.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.micronaut.serde.annotation.Serdeable
import java.util.*
import java.time.Instant

data class Action(
    val id: UUID,
    val tenantId: Long,
    val status: ActionStatus,
    val created: Instant,
    val triggerTime: Instant,
    val frequency: Frequency,
    val command: Command,
)

enum class ActionStatus {
    Pending, Completed
}

enum class Frequency {
    Single, Weekly, Biweekly, Monthly, Quarterly, SemiAnnually, Annually
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateFollowUpCommand::class, name = "followup")
)
@Serdeable
sealed class Command

data class CreateFollowUpCommand(
    val contactId: UUID,
    val note: String
) : Command()