package netman.api.contact

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
import netman.models.Contact2
import java.util.UUID

@Tag(name = "Contact", description = "API for managing contact resources")
interface ContactApi {
    @Operation(
        summary = "Get list of contacts for a tenant",
        responses = [ApiResponse(responseCode = "200", description = "List of contacts")]
    )
    @Get("/{tenantId}/contacts")
    fun getContactList(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long
    ) : List<ContactListItemResource>

    @Operation(
        summary = "Create a new contact with details",
        responses = [ApiResponse(responseCode = "201", description = "Contact created successfully")]
    )
    @Post("/{tenantId}/contacts")
    @Status(HttpStatus.CREATED)
    fun saveContactWithDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") @PathVariable tenantId: Long,
        @Parameter(description = "Contact information with details") @Body contactWithDetailsRequest: ContactResource
    ) : ContactResource

    @Operation(
        summary = "Get contact details by ID",
        responses = [ApiResponse(responseCode = "200", description = "Contact details")]
    )
    @Get("/{tenantId}/contacts/{contactId}")
    fun getContactDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "ID of the contact") contactId: UUID
    ) : ContactResource
}