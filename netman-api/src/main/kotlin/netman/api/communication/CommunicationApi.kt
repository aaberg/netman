package netman.api.communication

import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import netman.businesslogic.models.CommunicationResource
import netman.businesslogic.models.PageResource
import netman.businesslogic.models.RegisterCommunicationResource
import netman.businesslogic.models.SetCommunicationPropertiesResource
import java.util.*

@Tag(name = "Communication", description = "API for managing communication resources")
interface CommunicationApi {


    @Operation(
        summary = "Get communication by ID",
        description = "Retrieves a specific communication resource by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Communication found",
                content = [Content(schema = Schema(implementation = CommunicationResource::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Communication not found",
                content = [Content()]
            )
        ]
    )
    @Get("/{tenantId}/communications/{communicationId}")
    fun getCommunication(
        @Parameter(description = "The unique identifier of the tenant", required = true)
        tenantId: Long,
        @Parameter(description = "The unique identifier of the communication", required = true)
        communicationId: UUID,
    ): CommunicationResource?

    @Operation(
        summary = "Get communications by contact ID",
        description = "Retrieves all communication resources associated with a specific contact"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Communications found",
                content = [Content(schema = Schema(implementation = PageResource::class))]
            )
        ]
    )
    @Get("/{tenantId}/communications/{?contactId,page,pageSize}")
    fun getCommunications(
        @Parameter(description = "Tenant ID", required = true)
        tenantId: Long,
        @Parameter(description = "Contact ID")
        @QueryValue("contactId")
        contactId: UUID? = null,
        @Parameter(description = "The page number for pagination", required = false)
        @QueryValue("page")
        page: Int? = null,
        @Parameter(description = "The number of items per page", required = false)
        @QueryValue("pageSize")
        pageSize: Int? = null,
    ): PageResource<CommunicationResource>

    @Operation(
        summary = "Delete communication by ID",
        description = "Deletes a specific communication resource by its unique identifier"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Communication deleted successfully"
            ),
        ]
    )
    @Delete("{tenantId}/communications/{communicationId}")
    fun deleteCommunication(
        @Parameter(description = "The unique identifier of the tenant", required = true)
        tenantId: Long,
        @Parameter(description = "The unique identifier of the communication", required = true)
        communicationId: UUID)

    @Operation(
        summary = "Register a new communication",
        description = "Registers a new communication resource"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Communication registered successfully",
            )
        ]
    )
    @Post("/{tenantId}/communications")
    fun registerCommunication(
        @Parameter(description = "The unique identifier of the tenant", required = true)
        tenantId: Long,
        registerCommunicationApi: RegisterCommunicationResource)
    : CommunicationResource

    @Operation(
        summary = "Set communication properties",
        description = "Sets the properties of a communication resource"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Communication properties set successfully"
            ),
        ]
    )
    @Post("/{tenantId}/communications/{communicationId}/properties")
    fun setCommunicationProperties(
        @Parameter(description = "The unique identifier of the tenant", required = true)
        tenantId: Long,
        @Parameter(description = "The unique identifier of the communication", required = true)
        communicationId: UUID,
        setCommunicationPropertiesApi: SetCommunicationPropertiesResource)
}