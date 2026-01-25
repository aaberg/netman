package netman.access

import io.micronaut.data.model.Pageable
import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.ActionRepository
import netman.access.repository.DefaultTestProperties
import netman.businesslogic.MembershipManager
import netman.models.ActionStatus
import netman.models.CreateFollowUpCommand
import netman.models.Frequency
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionAccessTest : DefaultTestProperties() {

    @Inject
    lateinit var actionAccess: ActionAccess

    @Inject
    lateinit var actionRepository: ActionRepository

    @Inject
    lateinit var membershipManager: MembershipManager

    @Inject
    lateinit var objectMapper: ObjectMapper

    private fun createTenant(): Long {
        val userId = UUID.randomUUID().toString()
        val tenant = membershipManager.registerUserWithPrivateTenant(userId, "Test User")
        return tenant.id
    }

    @Test
    fun `registerNewAction should create and save action`() {
        // Arrange
        val tenantId = createTenant()
        val command = CreateFollowUpCommand(UUID.randomUUID(), "Follow up note")
        val triggerTime = Instant.now().plus(1, ChronoUnit.DAYS)
        val frequency = Frequency.Weekly

        // Act
        val action = actionAccess.registerNewAction(tenantId, command, triggerTime, frequency)

        // Assert
        assertThat(action.id).isNotNull
        assertThat(action.tenantId).isEqualTo(tenantId)
        assertThat(action.status).isEqualTo(ActionStatus.Pending)
        assertThat(action.command).isEqualTo(command)
        assertThat(action.triggerTime).isEqualTo(triggerTime)
        assertThat(action.frequency).isEqualTo(frequency)

        val savedDto = actionRepository.getById(action.id)
        assertThat(savedDto).isNotNull
        assertThat(savedDto!!.tenantId).isEqualTo(tenantId)
        assertThat(savedDto.status).isEqualTo(ActionStatus.Pending.toString())
        assertThat(savedDto.frequency).isEqualTo(frequency.toString())
        
        val deserializedCommand = objectMapper.readValue(savedDto.command, CreateFollowUpCommand::class.java)
        assertThat(deserializedCommand).isEqualTo(command)
    }

    @Test
    fun `getActions should return paged actions`() {
        // Arrange
        val tenantId = createTenant()
        val command = CreateFollowUpCommand(UUID.randomUUID(), "Note")
        val triggerTime = Instant.now()
        
        actionAccess.registerNewAction(tenantId, command, triggerTime, Frequency.Single)
        actionAccess.registerNewAction(tenantId, command, triggerTime, Frequency.Single)
        
        // Act
        val actionsPage = actionAccess.getActions(tenantId, ActionStatus.Pending, Pageable.from(0, 10))

        // Assert
        assertThat(actionsPage.content).hasSize(2)
        assertThat(actionsPage.content).allSatisfy { 
            assertThat(it.tenantId).isEqualTo(tenantId)
            assertThat(it.status).isEqualTo(ActionStatus.Pending)
        }
    }

    @Test
    fun `getAction should return action if it exists and belongs to tenant`() {
        // Arrange
        val tenantId = createTenant()
        val action = actionAccess.registerNewAction(tenantId, CreateFollowUpCommand(UUID.randomUUID(), "Note"), Instant.now(), Frequency.Single)

        // Act
        val retrievedAction = actionAccess.getAction(tenantId, action.id)

        // Assert
        assertThat(retrievedAction).isNotNull
        assertThat(retrievedAction!!.id).isEqualTo(action.id)
    }

    @Test
    fun `getAction should return null if action does not exist`() {
        // Arrange
        val tenantId = createTenant()

        // Act
        val retrievedAction = actionAccess.getAction(tenantId, UUID.randomUUID())

        // Assert
        assertThat(retrievedAction).isNull()
    }

    @Test
    fun `getAction should throw exception if action belongs to different tenant`() {
        // Arrange
        val tenantId1 = createTenant()
        val tenantId2 = createTenant()
        val action = actionAccess.registerNewAction(tenantId1, CreateFollowUpCommand(UUID.randomUUID(), "Note"), Instant.now(), Frequency.Single)

        // Act & Assert
        assertThatThrownBy {
            actionAccess.getAction(tenantId2, action.id)
        }.isInstanceOf(IllegalArgumentException::class.java)
         .hasMessageContaining("does not belong to tenant $tenantId2")
    }

    @Test
    fun `updateActionStatus should update status and return updated action`() {
        // Arrange
        val tenantId = createTenant()
        val action = actionAccess.registerNewAction(tenantId, CreateFollowUpCommand(UUID.randomUUID(), "Note"), Instant.now(), Frequency.Single)
        assertThat(action.status).isEqualTo(ActionStatus.Pending)

        // Act
        val updatedAction = actionAccess.updateActionStatus(action, ActionStatus.Completed)

        // Assert
        assertThat(updatedAction.status).isEqualTo(ActionStatus.Completed)
        assertThat(updatedAction.id).isEqualTo(action.id)
        
        val savedDto = actionRepository.getById(action.id)
        assertThat(savedDto!!.status).isEqualTo(ActionStatus.Completed.toString())
    }

    @Test
    fun `updateActionStatus should throw exception if action does not exist`() {
        // Arrange
        val action = netman.models.Action(
            id = UUID.randomUUID(),
            tenantId = 1L,
            status = ActionStatus.Pending,
            created = Instant.now(),
            triggerTime = Instant.now(),
            frequency = Frequency.Single,
            command = CreateFollowUpCommand(UUID.randomUUID(), "Note")
        )

        // Act & Assert
        assertThatThrownBy {
            actionAccess.updateActionStatus(action, ActionStatus.Completed)
        }.isInstanceOf(IllegalArgumentException::class.java)
         .hasMessageContaining("does not exist")
    }
}
