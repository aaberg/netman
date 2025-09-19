package netman.api.membership

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import reactor.core.publisher.Mono

/**
 * API for managing user membership profiles.
 * Provides endpoints for registering and retrieving user profiles.
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "cdMembership", description = "API for managing user membership profiles")
interface MembershipApi {

    @Operation(
        method = "PUT",
        summary = "Register a user profile",
        description = "Creates or updates a user profile with the given information"
    )
    @Parameters(
        io.swagger.v3.oas.annotations.Parameter(name = "userId", description = "The ID of the user", required = true)
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Profile successfully registered"
        ),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "User not found")
    )
    @Put("profile/{userId}", produces = ["application/json"])
    fun registerProfile(userId: String, @Body profile: ProfileResource) : Mono<HttpStatus>

    @Operation(
        method = "GET",
        summary = "Get a user profile",
        description = "Retrieves the profile information for the specified user"
    )
    @Parameters(
        io.swagger.v3.oas.annotations.Parameter(name = "userId", description = "The ID of the user", required = true)
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Profile found and returned"
        ),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "404", description = "Profile not found")
    )
    @Get("profile/{userId}", produces = ["application/json"])
    fun getProfile(userId: String) : Mono<ProfileResource?>
}

/**
 * Represents a user profile with basic information.
 *
 * @property name The display name of the user
 */
@Serdeable.Deserializable
@Serdeable.Serializable
data class ProfileResource(
    /** The display name of the user */
    val name: String,
    val initials: String
)