package netman.api.task

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.NetworkManager

@Controller("/api")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TaskApiController(
    private val networkManager: NetworkManager,
    private val taskResourceMapper: TaskResourceMapper
) : TaskApi {

    override fun createTaskWithTrigger(
        authentication: Authentication,
        request: CreateTaskWithTriggerRequest
    ): TaskResource {
        val userId = getUserId(authentication)
        val userUuid = java.util.UUID.fromString(userId)
        
        // Manually construct Task with userId and tenantId from request
        val task = netman.models.Task(
            id = request.task.id,
            userId = userUuid,
            tenantId = request.task.tenantId,
            data = request.task.data,
            status = request.task.status,
            created = request.task.created
        )
        
        val trigger = request.trigger?.let { triggerResource ->
            // targetTaskId will be set by NetworkManager, use a placeholder UUID for now
            val placeholderId = triggerResource.targetTaskId ?: java.util.UUID.randomUUID()
            taskResourceMapper.map(triggerResource.copy(targetTaskId = placeholderId))
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
