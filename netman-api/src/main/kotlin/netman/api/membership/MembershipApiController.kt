package netman.api.membership

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import netman.api.getUserId
import netman.businesslogic.MembershipManager
import reactor.core.publisher.Mono

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/membership")
@Consumes("application/json")
class MembershipApiController(val membershipManager: MembershipManager) : MembershipApi {

    override fun registerProfile(
        authentication: Authentication,
        profile: CreateProfileRequest
    ): Mono<HttpStatus> {
        val userId = getUserId(authentication)
        membershipManager.registerUserWithPrivateTenant(userId, profile.name)
        return Mono.just(HttpStatus.OK)
    }

    override fun getProfile(authentication: Authentication): Mono<ProfileResource?> {
        val userId = getUserId(authentication)
        val profile = membershipManager.getProfile(userId)
        return if (profile != null) Mono.just(ProfileResource(profile.name, profile.initials)) else Mono.empty()
    }

}
