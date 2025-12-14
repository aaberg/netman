package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable
import netman.models.FollowUpTask
import netman.models.TaskStatus
import netman.models.TaskType
import netman.models.TriggerStatus
import java.time.Instant
import java.util.*

@Serdeable
data class TaskResource(
    val id: UUID? = null,
    val tenantId: Long,
    val data: TaskType,
    val status: TaskStatus,
    val created: Instant? = null,
    val triggers: List<TriggerResource>
)

@Serdeable
data class CreateFollowUpTaskRequest(
    val data: FollowUpTask,
    val status: TaskStatus = TaskStatus.Pending,
    val trigger: CreateTriggerRequest? = null
)

@Serdeable
data class TriggerResource(
    val id: UUID? = null,
    val triggerType: String,
    val triggerTime: Instant,
    val targetTaskId: UUID? = null,
    val status: TriggerStatus,
    val statusTime: Instant? = null
)

@Serdeable
data class CreateTriggerRequest(
    val triggerType: String,
    val triggerTime: Instant,
)
