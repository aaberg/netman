package netman.api.task

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Task", description = "API for managing task resources")
interface TaskApi {
    @Operation(
        summary = "Create a new follow-up task with an optional trigger",
        responses = [ApiResponse(responseCode = "201", description = "Task created successfully")]
    )
    @Post("/{tenantId}/tasks")
    @Status(HttpStatus.CREATED)
    fun createTaskWithTrigger(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") @PathVariable tenantId: Long,
        @Parameter(description = "Task and optional trigger information") @Body request: CreateTaskWithTriggerRequest
    ): TaskResource

    @Operation(
        summary = "Get list of pending and due tasks for a tenant",
        responses = [ApiResponse(responseCode = "200", description = "List of pending and due tasks")]
    )
    @Get("/{tenantId}/tasks")
    fun listPendingAndDueTasks(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") @PathVariable tenantId: Long
    ): List<TaskResource>
}
