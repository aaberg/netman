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
