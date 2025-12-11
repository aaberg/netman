package netman.api.task

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Mapper
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import netman.businesslogic.TimeService
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
    val tenantId: Long,
    val data: TaskType,
    val status: TaskStatus,
    val created: Instant? = null
)

@Serdeable
data class CreateTaskRequest(
    val data: TaskType,
    val status: TaskStatus = TaskStatus.Pending
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

@Serdeable
data class CreateTaskWithTriggerRequest(
    val task: CreateTaskRequest,
    val trigger: CreateTriggerRequest? = null
)

@Bean
abstract class TaskResourceMapper {

    @Inject
    lateinit var timeService: TimeService

    @Mapper
    abstract fun mapToResource(task: Task): TaskResource

    @Mapper
    abstract fun map(trigger: Trigger): TriggerResource

    fun map(triggerResource: CreateTriggerRequest): Trigger {
        return Trigger(
            null,
            triggerResource.triggerType,
            triggerResource.triggerTime,
            null,
            TriggerStatus.Pending,
            timeService.now());
    }
}
