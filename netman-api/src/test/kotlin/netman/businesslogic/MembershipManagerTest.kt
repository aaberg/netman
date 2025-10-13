package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import netman.models.UserProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MembershipManagerTest : DefaultTestProperties() {

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

    @Test
    fun `registering same user twice updates profile and does not create new tenant`() {
        // Arrange
        val userId = UUID.randomUUID().toString()

        // Act
        val firstTenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        // sanity check: initial profile saved
        assertThat(membershipManager.getProfile(userId)).isEqualTo(UserProfile("Jane Doe", "JD"))

        val secondTenant = membershipManager.registerUserWithPrivateTenant(userId, "Janet Dee")
        val tenants = membershipManager.getMemberTenants(userId)

        // Assert
        assertThat(tenants).hasSize(1)
        assertThat(secondTenant.id).isEqualTo(firstTenant.id)
        assertThat(membershipManager.getProfile(userId)).isEqualTo(UserProfile("Janet Dee", "JD"))
    }
}
