package netman.access.client
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.annotation.Client
import io.micronaut.serde.annotation.Serdeable
import io.micronaut.serde.config.naming.SnakeCaseStrategy
import reactor.core.publisher.Mono
import java.time.Instant

@Client("\${hanko.base-url:`http://localhost:8000`}")
interface AuthenticationClient {

    @Post("/sessions/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun validateSession(@Body request: SessionValidationRequest) : Mono<SessionValidationResponse>
}

@Serdeable
data class SessionValidationRequest(val session_token: String)

@Serdeable.Deserializable(naming = SnakeCaseStrategy::class)
data class SessionValidationResponse(
    val isValid: Boolean,
    val expirationTime: Instant?,
    val userId: String?,
    val claims: SessionClaims?
)
@Serdeable.Deserializable(naming = SnakeCaseStrategy::class)
data class SessionClaims(
    val email: SessionEmail
)
@Serdeable.Deserializable(naming = SnakeCaseStrategy::class)
data class SessionEmail(
    val address: String,
    val isPrimary: Boolean,
    val isVerified: Boolean
)