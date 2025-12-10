package netman.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

enum class TaskStatus {
    Pending,
    Completed,
    Canceled,
    Due
}

@Introspected
data class Task(
    val id: UUID? = null,
    val userId: UUID,
    val tenantId: Long,
    val data: TaskType,
    val status: TaskStatus,
    val created: Instant? = null
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FollowUpTask::class, name = "followup")
)
@Serdeable(validate = false) @Introspected
sealed class TaskType

@Serdeable
data class FollowUpTask(
    val contactId: UUID,
    val note: String
) : TaskType()
