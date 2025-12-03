package netman.api.task

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import netman.access.client.setupAuthenticationClientForSuccessfullAuthentication
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.util.*

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
class TaskApiTest : DefaultTestProperties() {

    @Inject
    private lateinit var membershipManager: MembershipManager

    @Test
    fun `create a new task without trigger`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = UUID.randomUUID()
        
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Follow up with client about project proposal"
                        },
                        "status": "Pending"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", notNullValue())
            .body("status", equalTo("Pending"))
            .body("data.type", equalTo("followup"))
            .body("data.contactId", equalTo(contactId.toString()))
            .body("data.note", equalTo("Follow up with client about project proposal"))
    }

    @Test
    fun `create a new task with trigger`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = UUID.randomUUID()
        val triggerTime = Instant.now().plusSeconds(3600).toString()
        
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Follow up in one hour"
                        },
                        "status": "Pending"
                      },
                      "trigger": {
                        "triggerType": "scheduled",
                        "triggerTime": "$triggerTime",
                        "status": "Pending"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", notNullValue())
            .body("status", equalTo("Pending"))
            .body("data.type", equalTo("followup"))
            .body("data.note", equalTo("Follow up in one hour"))
    }

    @Test
    fun `list pending and due tasks for authenticated user`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = UUID.randomUUID()
        
        // Create a pending task
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Pending task 1"
                        },
                        "status": "Pending"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // Create a due task
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Due task 1"
                        },
                        "status": "Due"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // Create only completed tasks
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Completed task"
                        },
                        "status": "Completed"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // List pending and due tasks
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tasks")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].status", either(equalTo("Pending")).or(equalTo("Due")))
            .body("[1].status", either(equalTo("Pending")).or(equalTo("Due")))
    }

    @Test
    fun `list tasks returns empty list when no pending or due tasks exist`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = UUID.randomUUID()
        
        // Create only completed tasks
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Completed task"
                        },
                        "status": "Completed"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // List tasks - should be empty
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/tasks")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(0))
    }
}
