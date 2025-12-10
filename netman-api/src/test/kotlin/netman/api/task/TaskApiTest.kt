package netman.api.task

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.restassured.specification.RequestSpecification
import jakarta.inject.Inject
import netman.access.TenantAccess
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

    @Inject
    private lateinit var tenantAccess: TenantAccess

    @Test
    fun `create a new task without trigger`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
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
                        "tenantId": ${tenant.id},
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
            .body("tenantId", equalTo(tenant.id!!.toInt()))
            .body("status", equalTo("Pending"))
            .body("data.type", equalTo("followup"))
            .body("data.contactId", equalTo(contactId.toString()))
            .body("data.note", equalTo("Follow up with client about project proposal"))
    }

    @Test
    fun `create a new task with trigger`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
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
                        "tenantId": ${tenant.id},
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
            .body("tenantId", equalTo(tenant.id!!.toInt()))
            .body("status", equalTo("Pending"))
            .body("data.type", equalTo("followup"))
            .body("data.note", equalTo("Follow up in one hour"))
    }

    @Test
    fun `list pending and due tasks for authenticated user`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
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
                        "tenantId": ${tenant.id},
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
                        "tenantId": ${tenant.id},
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
                        "tenantId": ${tenant.id},
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
            .get("/api/${tenant.id}/tasks")
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
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
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
                        "tenantId": ${tenant.id},
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
            .get("/api/${tenant.id}/tasks")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `list tasks filtered by specific tenantId`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = UUID.randomUUID()
        
        // Create two pending tasks
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "tenantId": ${tenant.id},
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Task in tenant"
                        },
                        "status": "Pending"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // List tasks with tenantId in path
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/${tenant.id}/tasks")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].tenantId", equalTo(tenant.id!!.toInt()))
            .body("[0].data.note", equalTo("Task in tenant"))
    }

    @Test
    fun `list tasks from multiple tenants when tenantId not specified`(wmRuntimeInfo: WireMockRuntimeInfo, spec: RequestSpecification) {
        val userId = UUID.randomUUID().toString()
        val tenant1 = membershipManager.registerUserWithPrivateTenant(userId, "Jane Doe")
        // Register another tenant for the same user
        val tenant2 = tenantAccess.registerNewTenant("Second Tenant", netman.models.TenantType.ORGANIZATION, userId)
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo, userId)

        val contactId = UUID.randomUUID()
        
        // Create task in tenant1
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "tenantId": ${tenant1.id},
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Task in tenant 1"
                        },
                        "status": "Pending"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // Create task in tenant2
        spec.`when`()
            .auth().oauth2("dummy")
            .contentType("application/json")
            .body(
                """
                    {
                      "task": {
                        "tenantId": ${tenant2.id},
                        "data": {
                          "type": "followup",
                          "contactId": "$contactId",
                          "note": "Task in tenant 2"
                        },
                        "status": "Pending"
                      }
                    }
                """.trimIndent()
            )
            .post("/api/tasks")
        .then()
            .statusCode(201)

        // List tasks from tenant1 - should return only 1 task
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/${tenant1.id}/tasks")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].data.note", equalTo("Task in tenant 1"))

        // List tasks from tenant2 - should return only 1 task
        spec.`when`()
            .log().all()
            .auth().oauth2("dummy")
            .get("/api/${tenant2.id}/tasks")
        .then()
            .log().all()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].data.note", equalTo("Task in tenant 2"))
    }
}
