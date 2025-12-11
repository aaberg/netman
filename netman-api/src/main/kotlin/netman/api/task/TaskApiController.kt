package netman.api.task

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.NetworkManager
import netman.businesslogic.TimeService

@Controller("/api/tenants/")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TaskApiController(
    private val networkManager: NetworkManager,
    private val taskResourceMapper: TaskResourceMapper,
    private val timeService: TimeService
) : TaskApi {

    override fun createTaskWithTrigger(
        authentication: Authentication,
        tenantId: Long,
        request: CreateTaskWithTriggerRequest
    ): TaskResource {
        val userId = getUserId(authentication)
        val userUuid = java.util.UUID.fromString(userId)

        // Manually construct Task with userId and tenantId from request
        val task = netman.models.Task(
            userId = userUuid,
            tenantId = tenantId,
            data = request.task.data,
            status = request.task.status,
            created = timeService.now()
        )
        
        val trigger = request.trigger?.let { triggerResource ->
            taskResourceMapper.map(triggerResource)
        }
        
        val savedTask = networkManager.createTaskWithTrigger(userId, task, trigger)
        
        return taskResourceMapper.mapToResource(savedTask)
    }

    override fun listPendingAndDueTasks(
        authentication: Authentication,
        tenantId: Long
    ): List<TaskResource> {
        val userId = getUserId(authentication)
        val tasks = networkManager.listPendingAndDueTasks(userId, tenantId)
        return tasks.map { taskResourceMapper.mapToResource(it) }
    }
}
