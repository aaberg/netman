package netman.security

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.filters.AuthenticationFetcher
import jakarta.inject.Singleton
import netman.access.client.AuthenticationClient
import netman.access.client.SessionValidationRequest
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux

@Singleton
class BearerValidationFetcher(
    private val authenticationClient: AuthenticationClient
) : AuthenticationFetcher<HttpRequest<*>> {

    override fun fetchAuthentication(request: HttpRequest<*>?): Publisher<Authentication?>? {

        if (request == null) return Flux.empty()

        val token = getTokenFromCookie(request) ?: getTokenFromAuthorizationHeader(request) ?: ""

        if (token.isEmpty()) {
            return Flux.empty()
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

    fun authenticationFailed(): Flux<Authentication?> = Flux.error<Authentication>(
        AuthenticationException(
            AuthenticationFailed("Bearer validation failed")))

    fun getTokenFromCookie(request: HttpRequest<*>): String? {
        val cookies = request.cookies
        if (cookies?.contains("hanko") == true) {
            return cookies.get("hanko").value
        }
        return null
    }

    fun getTokenFromAuthorizationHeader(request: HttpRequest<*>): String? {
        val authHeader = request.headers?.get(HttpHeaders.AUTHORIZATION)
            ?: return null

        if (!authHeader.startsWith("Bearer ")) {
            return null
        }
        return authHeader.removePrefix("Bearer ").trim()
    }
}