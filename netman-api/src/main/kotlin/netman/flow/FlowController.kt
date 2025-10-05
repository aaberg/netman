package netman.flow

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.authentication.Authentication
import netman.api.getUserId
import netman.businesslogic.MembershipManager
import java.net.URI

@Controller("/flow")
class FlowController(val membershipManager: MembershipManager) {

    @Get("/authentication")
    fun startAuthenticationFlwo(authentication: Authentication?) : HttpResponse<Any> {
        if (authentication == null) {
            return HttpResponse.seeOther(URI.create("/auth/login"))
        }
        return continueAuthorizationFlow(authentication)
    }

    @Get("/authorization/continue")
    fun continueAuthorizationFlow(authentication: Authentication?) : HttpResponse<Any> {
        if (authentication == null) {
            return HttpResponse.unauthorized()
        }

        val userId = getUserId(authentication)

        val profile = membershipManager.getProfile(userId)
            ?: return HttpResponse.seeOther(URI.create("/auth/newprofile"))

        return HttpResponse.seeOther(URI.create("/app/dashboard"))
    }


}