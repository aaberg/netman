package netman.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.util.UUID

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FollowUpRepositoryTest : DefaultTestProperties() {

    @Inject
    lateinit var followUpRepository: FollowUpRepository

    @Inject
    lateinit var tenantRepository: TenantRepository

    @Test
    fun `save and fetch follow up by id`() {
        // Arrange
        val tenant = tenantRepository.save(TenantDTO(name = "ten", type = "Organization"))
        val followUpId = UUID.randomUUID()
        val contactId = UUID.randomUUID()
        val taskId = UUID.randomUUID()
        val followUp = FollowUpDTO(
            id = followUpId,
            tenantId = tenant.id!!,
            contactId = contactId,
            taskId = taskId,
            status = "OPEN",
            created = Instant.now(),
            note = "Some note"
        )

        // Act
        followUpRepository.save(followUp)
        val fetched = followUpRepository.getById(followUpId)

        // Assert
        assertThat(fetched).usingRecursiveComparison().ignoringFields("created").isEqualTo(followUp)
    }
}
