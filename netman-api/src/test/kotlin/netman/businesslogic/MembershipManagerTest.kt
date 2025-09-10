package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.RepositoryTestBase
import netman.models.UserProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MembershipManagerTest : RepositoryTestBase() {

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Test
    fun `validate that registering a new user with tenant also creates a profile`() {
        // Arrange
        val userId = UUID.randomUUID().toString()

        // Act
        membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        val profile = membershipManager.getProfile(userId)

        // Assert
        assertThat(profile).isEqualTo(UserProfile("Jane Doe", "JD"))
    }
}