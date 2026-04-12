package netman.api.contact

import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import netman.businesslogic.models.ContactDetailsResource
import netman.businesslogic.models.ContactListItemResource
import netman.businesslogic.models.ContactSavedResponse
import netman.businesslogic.models.SaveContactRequest
import netman.businesslogic.models.TemporaryImageUploadResponse
import java.util.UUID

@Tag(name = "Contact", description = "API for managing contacts")
@Secured(SecurityRule.IS_AUTHENTICATED)
interface ContactApi {

    @Operation(summary = "Get all contacts", description = "Returns a list of all contacts")
    @Parameters(
        Parameter(name = "tenantId", description = "Tenant Id"),
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of contacts retrieved successfully",
        ),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    @Get("/{tenantId}/contacts/")
    fun getContacts(authentication: Authentication, tenantId: Long) : List<ContactListItemResource>

    @Operation(summary = "Get details of a contact", description = "Returns details for a contact")
    @Parameters(
        Parameter(name = "tenantId", description = "Tenant Id"),
        Parameter(name = "userId", description = "User id"),
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Contacts retrieved successfully"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Not found")
    )
    @Get("/{tenantId}/contacts/{contactId}")
    fun getContactDetails(authentication: Authentication, tenantId: Long, contactId: UUID) : ContactDetailsResource

    @Operation(summary = "Creates or updates a contact", description = "Creates or updates a contact")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Contact created or updated successfully"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Attempt to update an existing contact that was not found")
    )
    @Post("/{tenantId}/contacts")
    fun saveContact(authentication: Authentication, tenantId: Long, @Body saveContactRequest: SaveContactRequest) :
            ContactSavedResponse

    @Operation(summary = "Upload contact image", description = "Stores image bytes for a contact")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Image stored successfully"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Contact not found")
    )
    @Put("/{tenantId}/contacts/{contactId}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    fun saveContactImage(
        authentication: Authentication,
        tenantId: Long,
        contactId: UUID,
        @Body image: ByteArray
    ): HttpStatus

    @Operation(summary = "Upload temporary contact image", description = "Uploads image before contact is saved")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Temporary image uploaded"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    @Put("/{tenantId}/contacts/images/temp")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    fun uploadTemporaryContactImage(
        authentication: Authentication,
        tenantId: Long,
        @Body image: ByteArray
    ): TemporaryImageUploadResponse
}
