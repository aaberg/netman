package netman.access.repository

import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.models.COMMAND_TYPE_FOLLOWUP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionRepositoryTest : DefaultTestProperties() {

    @Inject
    lateinit var actionRepository: ActionRepository

    @Inject
    lateinit var tenantRepository: TenantRepository

    @Test
    fun `save and fetch action by id`() {
        // Arrange
        val tenant = tenantRepository.save(TenantDTO(name = "ten", type = "Organization"))
        val actionId = UUID.randomUUID()
        val action = ActionDTO(
            id = actionId,
            tenantId = tenant.id!!,
            status = "PENDING",
            created = Instant.now(),
            triggerTime = Instant.now(),
            frequency = "DAILY",
            command = "{\"key\": \"value\"}",
            type = COMMAND_TYPE_FOLLOWUP
        )

        // Act
        actionRepository.save(action)
        val fetched = actionRepository.getById(actionId)

        // Assert
        assertThat(fetched).usingRecursiveComparison().ignoringFields("created", "triggerTime").isEqualTo(action)
    }

    @Test
    fun `find actions by tenant id`() {
        // Arrange
        val tenant1 = tenantRepository.save(TenantDTO(name = "tenant1", type = "Organization"))
        val tenant2 = tenantRepository.save(TenantDTO(name = "tenant2", type = "Organization"))

        requireNotNull(tenant1.id)
        requireNotNull(tenant2.id)

        val action1 = createAction(tenant1.id, "PENDING")
        val action2 = createAction(tenant1.id, "COMPLETED")
        val action3 = createAction(tenant2.id, "PENDING")

        actionRepository.save(action1)
        actionRepository.save(action2)
        actionRepository.save(action3)

        // Act
        val result = actionRepository.findByTenantId(tenant1.id, Pageable.from(0, 10))

        // Assert
        assertThat(result.content).hasSize(2)
        assertThat(result.content).extracting("id").containsExactlyInAnyOrder(action1.id, action2.id)
    }

    @Test
    fun `find actions by tenant id and status`() {
        // Arrange
        val tenant1 = tenantRepository.save(TenantDTO(name = "tenant1", type = "Organization"))

        requireNotNull(tenant1.id)

        val action1 = createAction(tenant1.id, "PENDING")
        val action2 = createAction(tenant1.id, "COMPLETED")
        val action3 = createAction(tenant1.id, "PENDING")

        actionRepository.save(action1)
        actionRepository.save(action2)
        actionRepository.save(action3)

        // Act
        val result = actionRepository.findByTenantIdAndStatus(tenant1.id!!, "PENDING", Pageable.from(0, 10))

        // Assert
        assertThat(result.content).hasSize(2)
        assertThat(result.content).extracting("id").containsExactlyInAnyOrder(action1.id, action3.id)
    }

    @Test
    fun `pagination works for find by tenant id`() {
        // Arrange
        val tenant = tenantRepository.save(TenantDTO(name = "tenant", type = "Organization"))
        requireNotNull(tenant.id)
        for (i in 1..5) {
            actionRepository.save(createAction(tenant.id, "PENDING"))
        }

        // Act & Assert
        val page1 = actionRepository.findByTenantId(tenant.id, Pageable.from(0, 2))
        assertThat(page1.content).hasSize(2)
        assertThat(page1.totalSize).isEqualTo(5)

        val page2 = actionRepository.findByTenantId(tenant.id, Pageable.from(1, 2))
        assertThat(page2.content).hasSize(2)

        val page3 = actionRepository.findByTenantId(tenant.id, Pageable.from(2, 2))
        assertThat(page3.content).hasSize(1)
    }

    private fun createAction(tenantId: Long, status: String): ActionDTO {
        return ActionDTO(
            id = UUID.randomUUID(),
            tenantId = tenantId,
            status = status,
            created = Instant.now(),
            triggerTime = Instant.now(),
            frequency = "DAILY",
            command = "{\"key\": \"value\"}",
            type = COMMAND_TYPE_FOLLOWUP
        )
    }
}
