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
class ContactApiTest() : DefaultTestProperties() {

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
                      "contact": {
                        "name": "someone", 
                        "initials": "S"
                      },
                      "details": []
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
        .then()
            .log().all()
            .statusCode(201)
            .body("contact.id", notNullValue())
            .body("contact.name", equalTo("someone"))
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
                      "contact": {
                        "name": "John Smith", 
                        "initials": "JS"
                      },
                      "details": [
                        {
                          "detail": {
                              "type": "email",
                              "address": "john.smith@example.com",
                              "isPrimary": true,
                              "label": "Personal"
                          }
                        },
                        {
                          "detail": {
                              "type": "phone",
                              "number": "+1234567890",
                              "label": "Work"
                          }
                        }
                      ]
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .log().all()
            .statusCode(201)
            .body("contact.id", notNullValue())
            .body("contact.name", equalTo("John Smith"))
            .body("details.size()", equalTo(2))
            .body("details[0].detail.type", equalTo("email"))
            .body("details[0].detail.address", equalTo("john.smith@example.com"))
            .body("details[1].detail.type", equalTo("phone"))
            .body("details[1].detail.number", equalTo("+1234567890"))
    }

    @Test
    fun `get contact details after creation`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Create contact with details first
        val contactId: Long = spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contact": {
                        "name": "Alice Wonderland"
                      },
                      "details": [
                        {
                          "detail": {
                            "type": "email",
                            "address": "alice@example.com",
                            "isPrimary": true,
                            "label": "Personal"
                          }
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
            .getLong("contact.id")

        // Get the contact details by id
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
            .log().all()
            .statusCode(200)
            .body("contact.id", equalTo(contactId.toInt()))
            .body("contact.name", equalTo("Alice Wonderland"))
            .body("contact.initials", equalTo("AW"))
            .body("details.size()", equalTo(1))
            .body("details[0].detail.type", equalTo("email"))
            .body("details[0].detail.address", equalTo("alice@example.com"))
    }

    @Test
    fun `update contact details and name`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Create a contact without details
        val contactId: Long = spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contact": {
                        "name": "Bob Builder",
                        "initials": "BB"
                      },
                      "details": []
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .log().all()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("contact.id")

        // Update the contact - change name and add details; also intentionally omit id in payload to test controller normalization
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contact": {
                        "name": "Robert Builder",
                        "initials": "RB"
                      },
                      "details": [
                        {
                          "detail": {
                            "type": "phone",
                            "number": "+4711111111",
                            "label": "Mobile"
                          }
                        }
                      ]
                    }
                """.trimIndent()
            )
            .put("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
            .log().all()
            .statusCode(200)

        // Fetch and verify the update
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
            .log().all()
            .statusCode(200)
            .body("contact.id", equalTo(contactId.toInt()))
            .body("contact.name", equalTo("Robert Builder"))
            .body("contact.initials", equalTo("RB"))
            .body("details.size()", equalTo(1))
            .body("details[0].detail.type", equalTo("phone"))
            .body("details[0].detail.number", equalTo("+4711111111"))
    }
}