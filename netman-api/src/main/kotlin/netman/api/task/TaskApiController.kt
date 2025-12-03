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
        
        val task = taskResourceMapper.map(request.task)
        val trigger = request.trigger?.let { 
            // Use a dummy UUID for targetTaskId as it will be replaced in NetworkManager
            taskResourceMapper.map(it.copy(targetTaskId = it.targetTaskId ?: java.util.UUID.randomUUID()))
        }
        
        val savedTask = networkManager.createTaskWithTrigger(userId, task, trigger)
        
        return taskResourceMapper.map(savedTask)
    }

    override fun listPendingAndDueTasks(
        authentication: Authentication
    ): List<TaskResource> {
        val userId = getUserId(authentication)
        val tasks = networkManager.listPendingAndDueTasks(userId)
        return tasks.map { taskResourceMapper.map(it) }
    }
}
