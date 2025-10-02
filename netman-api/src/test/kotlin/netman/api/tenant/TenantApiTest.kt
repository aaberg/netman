package netman.api.tenant

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import netman.access.client.setupAuthenticationClientForSuccessfullAuthentication
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class TenantApiTest() : DefaultTestProperties() {

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Test
    fun `get all tenants for a user`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "Jone Doe"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Act
        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .get("/api/tenants")
            .then()
                .log().all()
                .statusCode(200)
                .body("size()", `is`(1))
                .body("[0].tenant.name", `is`(tenant.name))
                .body("[0].tenant.tenantType", `is`(tenant.tenantType.toString()))
                .body("[0].role", `is`("Owner"))
    }

}