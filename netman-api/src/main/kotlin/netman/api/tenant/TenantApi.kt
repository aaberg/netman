package netman.api.tenant

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import netman.api.tenant.models.ContactResource
import netman.api.tenant.models.MemberTenantResource
import netman.businesslogic.models.ContactWithDetails

@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "Tenant", description = "API for managing tenant resources")
interface TenantApi {

    @Operation(
        summary = "Get all tenants for authenticated user",
        responses = [ApiResponse(responseCode = "200", description = "List of tenants")]
    )
    @Get(produces = ["application/json"])
    fun getTenants(authentication: Authentication) : List<MemberTenantResource>

    @Operation(
        summary = "Get specific tenant by ID",
        responses = [ApiResponse(responseCode = "200", description = "Tenant details")]
    )
    @Get("/{tenantId}")
    fun getTenant(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant to retrieve") tenantId: Long
    ): MemberTenantResource

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
        responses = [ApiResponse(responseCode = "200", description = "Contact created successfully")]
    )
    @Post("/{tenantId}/contacts")
    fun createContactWithDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "Contact information with details") contactWithDetails: ContactWithDetails
    )

    @Operation(
        summary = "Get contact details by ID",
        responses = [ApiResponse(responseCode = "200", description = "Contact details")]
    )
    @Get("/{tenantId}/contacts/{contactId}")
    fun getContactDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "ID of the contact") contactId: Long
    ) : ContactWithDetails

    @Operation(
        summary = "Update contact details",
        responses = [ApiResponse(responseCode = "200", description = "Contact updated successfully")]
    )
    @Put("/{tenantId}/contacts/{contactId}")
    fun updateContactWithDetails(
        authentication: Authentication,
        @Parameter(description = "ID of the tenant") tenantId: Long,
        @Parameter(description = "ID of the contact") contactId: Long,
        @Parameter(description = "Updated contact information") contactWithDetails: ContactWithDetails
    )
}