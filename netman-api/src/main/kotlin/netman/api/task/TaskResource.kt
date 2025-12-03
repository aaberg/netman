package netman.api.task

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Mapper
import io.micronaut.serde.annotation.Serdeable
import netman.models.Task
import netman.models.TaskStatus
import netman.models.TaskType
import netman.models.Trigger
import netman.models.TriggerStatus
import java.time.Instant
import java.util.*

@Serdeable
data class TaskResource(
    val id: UUID? = null,
    val data: TaskType,
    val status: TaskStatus,
    val created: Instant? = null
)

@Serdeable
data class TriggerResource(
    val id: UUID? = null,
    val triggerType: String,
    val triggerTime: Instant,
    val targetTaskId: UUID? = null,
    val status: TriggerStatus,
    val statusTime: Instant
)

@Serdeable
data class CreateTaskWithTriggerRequest(
    val task: TaskResource,
    val trigger: TriggerResource? = null
)

@Bean
abstract class TaskResourceMapper {
    @Mapper
    abstract fun mapToResource(task: Task): TaskResource

    @Mapper
    abstract fun map(trigger: Trigger): TriggerResource

    @Mapper
    abstract fun map(triggerResource: TriggerResource): Trigger
}
