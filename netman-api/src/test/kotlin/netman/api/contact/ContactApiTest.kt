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
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching

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

    @Test
    fun `test upload image and resolve imageUrl on contact details`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        val userId = UUID.randomUUID().toString()
        val userName = "Image User"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(SaveContactRequest(name = "Image Contact"))
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("id")

        val key = "t${tenant.id}-file-$contactId.png"
        val imagePublicUrl = "https://cdn.test/$key"

        stubFor(put(urlEqualTo("/file/$key")).willReturn(aResponse().withStatus(200)))
        stubFor(
            post(urlEqualTo("/file/$key/public-url")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"publicUrl\":\"$imagePublicUrl\"}")
            )
        )

        spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/octet-stream")
                .body(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))
                .put("/api/tenants/${tenant.id}/contacts/${contactId}/image")
            .then()
                .statusCode(200)

        spec
            .`when`()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/contacts/${contactId}")
            .then()
                .statusCode(200)
                .body("name", `is`("Image Contact"))
                .body("imageUrl", `is`(imagePublicUrl))
    }

    @Test
    fun `test upload image rejects unsupported content type by bytes`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        val userId = UUID.randomUUID().toString()
        val userName = "Invalid Image User"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(SaveContactRequest(name = "Invalid Image Contact"))
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("id")

        spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/octet-stream")
                .body("this-is-not-an-image".toByteArray())
                .put("/api/tenants/${tenant.id}/contacts/${contactId}/image")
            .then()
                .statusCode(400)
    }

    @Test
    fun `test upload temporary image and use tempFileId when saving contact`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        val userId = UUID.randomUUID().toString()
        val userName = "Temp Image User"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val tempFileId = "temp-123"
        val previewUrl = "https://cdn.test/temp-preview"
        stubFor(
            post(urlEqualTo("/temp-file")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"tempFileId\":\"$tempFileId\",\"expiresAt\":2000000000}")
            )
        )
        stubFor(
            post(urlEqualTo("/temp-file/$tempFileId/public-url")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"publicUrl\":\"$previewUrl\"}")
            )
        )

        val uploadedTempFileId = spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/octet-stream")
                .body(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))
                .put("/api/tenants/${tenant.id}/contacts/images/temp")
            .then()
                .statusCode(200)
                .body("previewUrl", `is`(previewUrl))
                .body("tempFileId", `is`(tempFileId))
                .body("mimeType", `is`("image/png"))
                .body("extension", `is`("png"))
                .extract()
                .jsonPath()
                .getString("tempFileId")

        stubFor(
            post(urlPathMatching("/temp-file/$tempFileId/promote/t${tenant.id}-file-.*\\.png"))
                .willReturn(aResponse().withStatus(200))
        )

        val contactId = spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(
                    SaveContactRequest(
                        name = "Temp Contact",
                        tempFileId = uploadedTempFileId,
                        tempFileMimeType = "image/png",
                        tempFileExtension = "png"
                    )
                )
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("id")

        val expectedFinalKey = "t${tenant.id}-file-$contactId.png"
        val imagePublicUrl = "https://cdn.test/$expectedFinalKey"
        stubFor(
            post(urlEqualTo("/file/$expectedFinalKey/public-url")).willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"publicUrl\":\"$imagePublicUrl\"}")
            )
        )

        spec
            .`when`()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/contacts/$contactId")
            .then()
                .statusCode(200)
                .body("imageUrl", `is`(imagePublicUrl))
    }

    @Test
    fun `test save contact rejects missing temporary file`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        val userId = UUID.randomUUID().toString()
        val userName = "Expired Temp Image User"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(SaveContactRequest(name = "Existing Contact"))
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("id")

        stubFor(
            post(urlEqualTo("/temp-file/temp-missing/promote/t${tenant.id}-file-$contactId.png"))
                .willReturn(aResponse().withStatus(404))
        )

        spec
            .`when`()
                .auth().oauth2("dummy")
                .contentType("application/json")
                .body(
                    SaveContactRequest(
                        id = UUID.fromString(contactId),
                        name = "Temp Contact",
                        tempFileId = "temp-missing",
                        tempFileMimeType = "image/png",
                        tempFileExtension = "png"
                    )
                )
                .post("/api/tenants/${tenant.id}/contacts")
            .then()
                .statusCode(400)
    }
}
