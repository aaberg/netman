package netman.api.tenant

import io.micronaut.http.annotation.Get
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import netman.businesslogic.models.MemberTenantResource

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

    @Get("/default")
    fun getDefaultTenant(authentication: Authentication) : MemberTenantResource
}