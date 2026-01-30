package netman.api.task

import io.micronaut.data.annotation.Query
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import netman.businesslogic.models.CreateFollowUpTaskRequest
import netman.businesslogic.models.FollowUpActionResource
import netman.businesslogic.models.PageResource
import netman.businesslogic.models.PageableResource
import netman.businesslogic.models.RegisterScheduledFollowUpRequest
import netman.businesslogic.models.TaskResource

@Tag(name = "Task", description = "API for managing task resources")
interface TaskApi {

    @Operation(
        summary = "Register a scheduled follow-up",
        responses = [ApiResponse(responseCode = "201", description = "Follow-up registered successfully")]
    )
    @Post("/{tenantId}/scheduled-follow-ups")
    @Status(HttpStatus.CREATED)
    fun registerScheduledFollowUp(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") @PathVariable tenantId: Long,
        @Parameter(description = "Registe Follow-Up Request") @Body request: RegisterScheduledFollowUpRequest
    ) : FollowUpActionResource

    @Operation(
        summary = "Get list of pending follow-ups for a tenant",
        responses = [ApiResponse(responseCode = "200", description = "List of pending follow-ups")]
    )
    @Get("/{tenantId}/scheduled-follow-ups")
    fun getMyPendingFollowUps(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "Page", required = false) @QueryValue page: Int?,
        @Parameter(description = "PageSize", required = false) @QueryValue pageSize: Int?
    ) : PageResource<FollowUpActionResource>
}
