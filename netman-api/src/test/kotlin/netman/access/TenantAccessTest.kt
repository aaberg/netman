package netman.access

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import netman.models.MemberTenant
import netman.models.TenantRole
import netman.models.TenantType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantAccessTest : DefaultTestProperties() {

    @Inject
    lateinit var tenantAccess: TenantAccess

    @Test
    fun `register some tenants and fetch them`() {
        // Arrange
        val user1 = "user-id-1234"
        val user2 = "user-id-4321"

        val tenant1 = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, user1)
        val tenant2 = tenantAccess.registerNewTenant("tenant2", TenantType.ORGANIZATION, user1)
        tenantAccess.registerNewTenant("tenant3", TenantType.ORGANIZATION, user2)

        // Act
        val tenants = tenantAccess.getMemberTenants(user1)

        // Assert

        assertThat(tenants).containsExactlyInAnyOrder(
            MemberTenant( tenant1, user1, TenantRole.Owner),
            MemberTenant( tenant2, user1, TenantRole.Owner))
    }

    @Test
    fun `associate a member to a new tenant`() {
        // Arrange
        val user1 = "user-id-1234"
        val user2 = "user-id-4321"

        val tenant1 = tenantAccess.registerNewTenant("tenant1", TenantType.PERSONAL, user1)
        val tenant2 = tenantAccess.registerNewTenant("tenant2", TenantType.PERSONAL, user2)

        // Act
        tenantAccess.associateMemberToTenant(user1, tenant2, TenantRole.Member)

        val user1Tenants = tenantAccess.getMemberTenants(user1)

        // Assert
        assertThat(user1Tenants).containsExactlyInAnyOrder(
            MemberTenant(tenant1, user1, TenantRole.Owner),
            MemberTenant(tenant2, user1, TenantRole.Member))
    }
}