package netman.api.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileRepositoryTest : RepositoryTestBase() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Test
    fun `test save and find profile by id`() {
        // Arrange
        val userId = UUID.randomUUID()
        val profile = ProfileDTO(userId, "testname")

        // Act
        profileRepository.save(profile)
        val fetchedProfile = profileRepository.getByUserId(userId)

        // Assert
        assertThat(fetchedProfile).isEqualTo(profile)
    }

    @Test
    fun `test override existing profile`() {
        // Arrange
        val userId = UUID.randomUUID()
        val originalProfile = ProfileDTO(userId, "testname")
        val newProfile = originalProfile.copy(name = "newname")

        // Act
        profileRepository.save(originalProfile)
        profileRepository.update(newProfile)
        val fetchedProfile = profileRepository.getByUserId(userId)

        // Assert
        assertThat(fetchedProfile).isEqualTo(newProfile)
    }
    
    @Test
    fun `verify that fetching a non-existing profile returns null`() {
        // Arrange
        val nonExistingId = UUID.randomUUID()

        // Act
        val fetchedProfile = profileRepository.getByUserId(nonExistingId)

        // Assert
        assertThat(fetchedProfile).isNull()
    }
}