package netman.api.testing

import io.micronaut.http.HttpRequest
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Testing", description = "API for testing")
@Controller("/api/testing")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TestingController {

    @Get("/session-token")
    fun getSessionToken(request: HttpRequest<Any>) : String {
        request.cookies.get("hanko")?.let {
            return it.value
        }
        return "No session token found in request header"
    }
}