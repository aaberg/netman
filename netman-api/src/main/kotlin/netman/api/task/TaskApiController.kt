package netman.api.task

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.TaskManager
import netman.businesslogic.models.FollowUpActionResource
import netman.businesslogic.models.PageResource
import netman.businesslogic.models.PageableResource
import netman.businesslogic.models.RegisterFollowUpRequest
import netman.businesslogic.models.RegisterScheduledFollowUpRequest

@Controller("/api/tenants/")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TaskApiController(
    private val taskManager: TaskManager,
) : TaskApi {

    override fun registerScheduledFollowUp(
        authentication: Authentication,
        tenantId: Long,
        request: RegisterScheduledFollowUpRequest
    ): FollowUpActionResource {
        val userId = getUserId(authentication)
        return taskManager.registerScheduledFollowUp(userId, tenantId, request)
    }

    override fun registerFollowUpV2(
        authentication: Authentication,
        tenantId: Long,
        request: RegisterFollowUpRequest
    ): FollowUpActionResource {
        val userId = getUserId(authentication)
        return taskManager.registerFollowUp(userId, tenantId, request)
    }

    override fun getMyPendingFollowUps(
        authentication: Authentication,
        tenantId: Long,
        page: Int?,
        pageSize: Int?
    ): PageResource<FollowUpActionResource> {
        val userId = getUserId(authentication)
        return taskManager.getPendingFollowUps(userId, tenantId, PageableResource(page ?: 0, pageSize ?: 10))
    }
}
