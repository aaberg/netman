package netman.api.membership

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import netman.access.client.setupAuthenticationClientForSuccessfullAuthentication
import netman.access.repository.DefaultTestProperties
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
class MembershipApiTest : DefaultTestProperties() {

    @Test
    fun `test register and get profile`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = "062a7851-88e2-41aa-aeb4-dcad0c3bcf34"
        val userName = "John Doe"
        val initials = "JD"
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)


        // Act & Assert - put profile
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body("""
                {
                  "name": "${userName}",
                  "initials": "${initials}"
                }
            """.trimIndent())
            .put("/api/membership/profile/${userId}")
        .then()
            .log().all()
            .statusCode(200)

        // Act & Assert - get profile
        spec.`when`()
        .log().all()
            .auth().oauth2("dummy")
            .get("/api/membership/profile/${userId}")
            .then()
        .log().all()
            .statusCode(200)
            .body("name", `is`(userName))
            .body("initials", `is`(initials))
    }
}