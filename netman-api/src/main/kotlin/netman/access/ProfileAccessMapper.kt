package netman.access

import io.micronaut.context.annotation.Mapper
import netman.access.repository.ProfileDTO
import netman.models.UserProfile
import java.util.UUID

@Mapper
interface ProfileAccessMapper {

    @Mapper
    fun toUserProfile(dto: ProfileDTO) : UserProfile
}