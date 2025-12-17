package netman.api.membership

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.MembershipManager
import netman.businesslogic.models.ProfileResource

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/membership")
@Consumes("application/json")
class MembershipApiController(val membershipManager: MembershipManager) : MembershipApi {

    override fun registerProfile(
        authentication: Authentication,
        profile: CreateProfileRequest
    ): HttpStatus {
        val userId = getUserId(authentication)
        membershipManager.registerUserWithPrivateTenant(userId, profile.name)
        return HttpStatus.OK
    }

    override fun getProfile(authentication: Authentication): ProfileResource? {
        val userId = getUserId(authentication)
        return membershipManager.getProfile(userId)
    }

}
