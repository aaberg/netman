package netman.api.access

import jakarta.inject.Singleton
import netman.api.access.repository.ProfileDTO
import netman.api.access.repository.ProfileRepository
import netman.api.models.UserProfile
import java.util.*

@Singleton
class ProfileAccess(val profileRepository: ProfileRepository) {

    fun storeProfile(userId: String, userProfile: UserProfile) {
        val userId = UUID.fromString(userId)
        val existingProfile = profileRepository.getByUserId(userId)

        if (existingProfile == null) {
            profileRepository.save(ProfileDTO(userId, userProfile.name))
        } else {
            profileRepository.update(ProfileDTO(userId, userProfile.name))
        }
    }

    fun getProfile(userId:String) : UserProfile? {
        val userId = UUID.fromString(userId)
        val profileDto = profileRepository.getByUserId(userId) ?: return null
        return UserProfile(profileDto.name)
    }
}