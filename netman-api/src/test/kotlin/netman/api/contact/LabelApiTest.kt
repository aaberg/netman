package netman.api.contact

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import netman.access.client.setupAuthenticationClientForSuccessfullAuthentication
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class LabelApiTest : DefaultTestProperties() {

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Test
    fun `get labels returns common labels when tenant is created`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/labels")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("label", hasItems("Home", "Work"))
    }

    @Test
    fun `labels are added when contact with labeled details is saved`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Create a contact with labeled details
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "name": "John Smith",
                      "details": [
                        {
                          "type": "email",
                          "address": "john.smith@example.com",
                          "isPrimary": true,
                          "label": "Personal"
                        },
                        {
                          "type": "phone",
                          "number": "+1234567890",
                          "label": "Mobile"
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
        .then()
            .statusCode(201)

        // Verify labels include common labels and new labels from contact
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/labels")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(4))
            .body("label", hasItems("Home", "Work", "Personal", "Mobile"))
    }

    @Test
    fun `duplicate labels are not added multiple times`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Create first contact with "Home" label
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "name": "Contact One",
                      "details": [
                        {
                          "type": "email",
                          "address": "one@example.com",
                          "isPrimary": true,
                          "label": "Home"
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
        .then()
            .statusCode(201)

        // Create second contact with "Home" label again
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "name": "Contact Two",
                      "details": [
                        {
                          "type": "phone",
                          "number": "+9876543210",
                          "label": "Home"
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
        .then()
            .statusCode(201)

        // Verify "Home" appears only once
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/labels")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("label", hasItems("Home", "Work"))
    }
}
