package netman.api.contact

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Status
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

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
    ) : List<ContactResource>

    @Operation(
        summary = "Create a new contact with details",
        responses = [ApiResponse(responseCode = "201", description = "Contact created successfully")]
    )
    @Post("/{tenantId}/contacts")
    @Status(HttpStatus.CREATED)
    fun createContactWithDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") @PathVariable tenantId: Long,
        @Parameter(description = "Contact information with details") @Body contactWithDetailsRequest: ContactWithDetailsResource
    ) : ContactWithDetailsResource

    @Operation(
        summary = "Get contact details by ID",
        responses = [ApiResponse(responseCode = "200", description = "Contact details")]
    )
    @Get("/{tenantId}/contacts/{contactId}")
    fun getContactDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "ID of the contact") contactId: Long
    ) : ContactWithDetailsResource

    @Operation(
        summary = "Update contact details",
        responses = [ApiResponse(responseCode = "200", description = "Contact updated successfully")]
    )
    @Put("/{tenantId}/contacts/{contactId}")
    fun updateContactWithDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "ID of the contact") contactId: Long,
        @Parameter(description = "Updated contact information") @Body contactWithDetails: ContactWithDetailsResource
    )
}