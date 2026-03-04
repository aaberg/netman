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
    
    @Test
    fun `register a communication for a contact`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)
        
        // Create a contact first
        val contactId = spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body("""{"name": "John Doe", "details": []}""")
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getUUID("id")
        
        // Register a communication
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contactId": "$contactId",
                      "type": "EMAIL",
                      "content": "Sent project proposal via email",
                      "timestamp": "2026-03-04T10:00:00Z",
                      "metadata": {
                        "subject": "Project Proposal"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts/$contactId/communications")
            .then()
            .log().all()
            .statusCode(201)
            .body("id", notNullValue())
            .body("contactId", equalTo(contactId.toString()))
            .body("type", equalTo("EMAIL"))
            .body("content", equalTo("Sent project proposal via email"))
            .body("metadata.subject", equalTo("Project Proposal"))
    }
    
    @Test
    fun `get communications for a contact`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)
        
        // Create a contact first
        val contactId = spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body("""{"name": "Jane Smith", "details": []}""")
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getUUID("id")
        
        // Register multiple communications
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contactId": "$contactId",
                      "type": "EMAIL",
                      "content": "First email",
                      "timestamp": "2026-03-04T10:00:00Z",
                      "metadata": {"subject": "Hello"}
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts/$contactId/communications")
            .then()
            .statusCode(201)
        
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contactId": "$contactId",
                      "type": "CALL",
                      "content": "Discussed project timeline",
                      "timestamp": "2026-03-04T11:00:00Z",
                      "metadata": {"duration": "30 minutes"}
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts/$contactId/communications")
            .then()
            .statusCode(201)
        
        // Get all communications for the contact
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tenants/${tenant.id}/contacts/$contactId/communications")
            .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].communication.contactId", equalTo(contactId.toString()))
            .body("[0].contact.id", equalTo(contactId.toString()))
            .body("[0].contact.name", equalTo("Jane Smith"))
    }
    
    @Test
    fun `register communication with text message type`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)
        
        // Create a contact first
        val contactId = spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body("""{"name": "Bob Wilson", "details": []}""")
            .post("/api/tenants/${tenant.id}/contacts")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getUUID("id")
        
        // Register a text message communication
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "contactId": "$contactId",
                      "type": "TEXT_MESSAGE",
                      "content": "Quick update via SMS",
                      "timestamp": "2026-03-04T12:00:00Z",
                      "metadata": {}
                    }
                """.trimIndent()
            )
            .post("/api/tenants/${tenant.id}/contacts/$contactId/communications")
            .then()
            .log().all()
            .statusCode(201)
            .body("type", equalTo("TEXT_MESSAGE"))
            .body("content", equalTo("Quick update via SMS"))
    }
}