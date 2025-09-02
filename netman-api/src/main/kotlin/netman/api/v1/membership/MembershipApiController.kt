package netman.api.v1.membership

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import netman.api.businesslogic.MembershipManager
import reactor.core.publisher.Mono

@Controller("/api/v1/membership")
class MembershipApiController(val membershipManager: MembershipManager) : MembershipApi {

    override fun registerProfile(
        userId: String,
        profile: Profile
    ): Mono<HttpStatus> {
        membershipManager.registerUserWithPrivateTenant(userId, profile.name)
        return Mono.just(HttpStatus.OK)
    }

    override fun getProfile(userId: String): Mono<Profile?> {
        val profile = membershipManager.getProfile(userId);
        return if (profile != null) Mono.just(Profile(profile.name)) else Mono.empty()
    }

}