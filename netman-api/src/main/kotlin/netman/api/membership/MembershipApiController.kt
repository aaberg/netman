package netman.api.membership

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import netman.businesslogic.MembershipManager
import reactor.core.publisher.Mono

@Controller("/api/membership")
class MembershipApiController(val membershipManager: MembershipManager) : MembershipApi {

    override fun registerProfile(
        userId: String,
        profile: ProfileResource
    ): Mono<HttpStatus> {
        membershipManager.registerUserWithPrivateTenant(userId, profile.name)
        return Mono.just(HttpStatus.OK)
    }

    override fun getProfile(userId: String): Mono<ProfileResource?> {
        val profile = membershipManager.getProfile(userId);
        return if (profile != null) Mono.just(ProfileResource(profile.name, profile.initials)) else Mono.empty()
    }

}
