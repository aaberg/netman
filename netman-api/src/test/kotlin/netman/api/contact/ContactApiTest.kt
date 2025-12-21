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
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class ContactApiTest : DefaultTestProperties() {

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Test
    fun `create a new contact with empty details`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "name": "someone",
                      "details": []
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("someone"))
    }
    
    @Test
    fun `create a new contact with details`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

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
                          "label": "Work"
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .log().all()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("John Smith"))
            .body("details.size()", equalTo(2))
            .body("details[0].type", equalTo("email"))
            .body("details[0].address", equalTo("john.smith@example.com"))
            .body("details[1].type", equalTo("phone"))
            .body("details[1].number", equalTo("+1234567890"))
    }

    @Test
    fun `get contact details after creation`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Create contact with details first
        val contactId: UUID = spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "name": "Alice Wonderland",
                      "details": [
                        {
                          "type": "email",
                          "address": "alice@example.com",
                          "isPrimary": true,
                          "label": "Personal"
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .log().all()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getUUID("id")

        // Get the contact details by id
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(contactId.toString()))
            .body("name", equalTo("Alice Wonderland"))
            .body("initials", equalTo("AW"))
            .body("details.size()", equalTo(1))
            .body("details[0].type", equalTo("email"))
            .body("details[0].address", equalTo("alice@example.com"))
    }

    @Test
    fun `update contact details and name`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Create a contact without details
        val contactId = spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "name": "Bob Builder",
                      "details": []
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts/")
            .then()
            .log().all()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getUUID("id")

        // Update the contact - change name and add details; also intentionally omit id in payload to test controller normalization
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "id": "$contactId",
                      "name": "Robert Builder",
                      "initials": "RB",
                      "details": [
                        {
                          "type": "phone",
                          "number": "+4711111111",
                          "label": "Mobile"
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts/")
            .then()
            .log().all()
            .statusCode(201)

        // Fetch and verify the update
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(contactId.toString()))
            .body("name", equalTo("Robert Builder"))
            .body("initials", equalTo("RB"))
            .body("details.size()", equalTo(1))
            .body("details[0].type", equalTo("phone"))
            .body("details[0].number", equalTo("+4711111111"))
    }
}