package netman.api.task

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.NetworkManager
import netman.businesslogic.TimeService
import netman.businesslogic.models.CreateFollowUpTaskRequest
import netman.businesslogic.models.TaskResource

@Controller("/api/tenants/")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TaskApiController(
    private val networkManager: NetworkManager,
    private val timeService: TimeService
) : TaskApi {

    override fun createTaskWithTrigger(
        authentication: Authentication,
        tenantId: Long,
        request: CreateFollowUpTaskRequest
    ): TaskResource {
        val userId = getUserId(authentication)
        return networkManager.createTaskWithTrigger(userId, tenantId, request)
    }

    override fun listPendingAndDueTasks(
        authentication: Authentication,
        tenantId: Long
    ): List<TaskResource> {
        val userId = getUserId(authentication)
        return networkManager.listPendingAndDueTasks(userId, tenantId)
    }
}
