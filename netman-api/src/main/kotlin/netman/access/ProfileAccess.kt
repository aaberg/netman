package netman.access

import jakarta.inject.Singleton
import netman.access.repository.ProfileDTO
import netman.access.repository.ProfileRepository
import netman.models.UserProfile
import java.util.*

@Singleton
class ProfileAccess(
    val profileRepository: ProfileRepository,
    val profileAccessMapper: ProfileAccessMapper
) {

    fun storeProfile(userId: String, userProfile: UserProfile) {
        val userId = UUID.fromString(userId)
        val existingProfile = profileRepository.getByUserId(userId)

        //val dto = ProfileDTO(userId, userProfile.name, "")
        val dto = ProfileDTO(userId, userProfile.name, userProfile.initials)
        if (existingProfile == null) {
            profileRepository.save(dto)
        } else {
            profileRepository.update(dto)
        }
    }

    fun getProfile(userId:String) : UserProfile? {
        val userId = UUID.fromString(userId)
        val profileDto = profileRepository.getByUserId(userId) ?: return null
        return profileAccessMapper.toUserProfile(profileDto)
    }
}