package netman.api.contact

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import netman.access.ContactAccess
import netman.access.client.setupAuthenticationClientForSuccessfullAuthentication
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import netman.businesslogic.models.SaveContactRequest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import jakarta.inject.Inject

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class ContactApiTest : DefaultTestProperties() {

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Inject
    private lateinit var contactAccess: ContactAccess

    @Test
    fun `test get contacts list`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "John Doe"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Act & Assert
        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/contacts/")
            .then()
                .log().all()
                .statusCode(200)
                .body("size()", `is`(0)) // Empty list initially
    }

    @Test
    fun `test save and get contact details`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "Jane Smith"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactRequest = SaveContactRequest(
            name = "Test Contact",
            email = "test@example.com",
            phone = "+1234567890",
            title = "Developer",
            organization = "Test Corp",
            notes = "Test notes"
        )

        // Act & Assert - save contact
        val contactId = spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(contactRequest)
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .jsonPath()
                .getString("id")

        // Act & Assert - get contact details
        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
                .log().all()
                .statusCode(200)
                .body("name", `is`("Test Contact"))
                .body("email", `is`("test@example.com"))
                .body("phone", `is`("+1234567890"))
                .body("title", `is`("Developer"))
                .body("organization", `is`("Test Corp"))
                .body("notes", `is`("Test notes"))
    }

    @Test
    fun `test get contacts list with saved contact`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "Bob Johnson"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Save a contact first
        val contactRequest = SaveContactRequest(
            name = "Another Contact",
            email = "another@example.com",
            title = "Manager"
        )

        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(contactRequest)
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .log().all()
                .statusCode(200)

        // Act & Assert - get contacts list
        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/contacts/")
            .then()
                .log().all()
                .statusCode(200)
                .body("size()", `is`(1))
                .body("[0].name", `is`("Another Contact"))
    }

    @Test
    fun `test update existing contact`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "Alice Brown"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Save a contact first
        val createRequest = SaveContactRequest(
            name = "Original Name",
            email = "original@example.com",
            phone = "+1111111111"
        )

        val contactId = spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(createRequest)
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .jsonPath()
                .getString("id")

        // Update the contact
        val updateRequest = SaveContactRequest(
            id = UUID.fromString(contactId),
            name = "Updated Name",
            email = "updated@example.com",
            phone = "+2222222222",
            title = "Senior Developer",
            organization = "Updated Corp",
            notes = "This contact has been updated"
        )

        // Act & Assert - update contact
        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(updateRequest)
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .log().all()
                .statusCode(200)
                .body("id", `is`(contactId))

        // Verify the update
        spec
            .`when`()
                .log().all()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
                .log().all()
                .statusCode(200)
                .body("name", `is`("Updated Name"))
                .body("email", `is`("updated@example.com"))
                .body("phone", `is`("+2222222222"))
                .body("title", `is`("Senior Developer"))
                .body("organization", `is`("Updated Corp"))
                .body("notes", `is`("This contact has been updated"))
    }
}