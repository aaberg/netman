package netman.api.tenant

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import netman.access.ActionAccess
import netman.access.ContactAccess
import netman.access.client.setupAuthenticationClientForSuccessfullAuthentication
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import netman.models.CreateFollowUpCommand
import netman.models.Frequency
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.util.*

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class TenantApiTest : DefaultTestProperties() {

    @Inject
    private lateinit var membershipManager: MembershipManager
    
    @Inject
    private lateinit var contactAccess: ContactAccess
    
    @Inject
    private lateinit var actionAccess: ActionAccess

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

    @Test
    fun `get tenant summary returns valid response`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "Test User"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)
        
        // Create some test data
        contactAccess.saveContact(tenant.id, netman.models.Contact2(
            id = UUID.randomUUID(),
            name = "Test Contact",
            details = listOf()
        ))
        
        actionAccess.registerNewAction(
            tenant.id,
            CreateFollowUpCommand(UUID.randomUUID(), "Test follow-up"),
            Instant.now().plusSeconds(3600),
            Frequency.Single
        )
        
        actionAccess.registerFollowUp(
            tenant.id,
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Test follow-up note"
        )

        // Act & Assert
        spec
            .`when`()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/summary")
            .then()
                .statusCode(200)
                .body("tenantId", notNullValue())
                .body("numberOfContacts", `is`(1))
                .body("numberOfPendingActions", `is`(1))
                .body("pendingFollowUps", notNullValue())
                .body("pendingFollowUps.size()", `is`(1))
                .body("pendingFollowUps[0].id", notNullValue())
                .body("pendingFollowUps[0].contactId", notNullValue())
                .body("pendingFollowUps[0].taskId", notNullValue())
                .body("pendingFollowUps[0].note", `is`("Test follow-up note"))
                .body("pendingFollowUps[0].created", notNullValue())
    }

    @Test
    fun `get tenant summary returns empty data for new tenant`(
        wmRuntimeInfo: WireMockRuntimeInfo,
        spec: RequestSpecification
    ) {
        // Arrange
        val userId = UUID.randomUUID().toString()
        val userName = "Test User"
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, userName)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        // Act & Assert
        spec
            .`when`()
                .auth().oauth2("dummy")
                .get("/api/tenants/${tenant.id}/summary")
            .then()
                .statusCode(200)
                .body("tenantId", `is`(tenant.id.toInt()))
                .body("numberOfContacts", `is`(0))
                .body("numberOfPendingActions", `is`(0))
    }
}