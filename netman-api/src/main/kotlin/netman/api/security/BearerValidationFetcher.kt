package netman.api.security

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.filters.AuthenticationFetcher
import jakarta.inject.Singleton
import netman.api.access.client.AuthenticationClient
import netman.api.access.client.SessionValidationRequest
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux

@Singleton
class BearerValidationFetcher(
    private val authenticationClient: AuthenticationClient
) : AuthenticationFetcher<HttpRequest<*>> {

    override fun fetchAuthentication(request: HttpRequest<*>?): Publisher<Authentication?>? {
        val authHeader = request?.headers?.get(HttpHeaders.AUTHORIZATION)
            ?: return Flux.empty()

        fun authenticationFailed() = Flux.error<Authentication>(
            AuthenticationException(
                AuthenticationFailed("Bearer validation failed")))

        if (!authHeader.startsWith("Bearer ")) {
            return authenticationFailed()
        }
        val token = authHeader.removePrefix("Bearer ").trim()
        if (token.isEmpty()) {
            return authenticationFailed()
        }

        return Flux.from(authenticationClient.validateSession(SessionValidationRequest(token)))
            .flatMap { response ->
                if (response.isValid) {
                    Flux.just(Authentication.build(response.userId!!))
                } else {
                    authenticationFailed()
                }
            }
    }


}