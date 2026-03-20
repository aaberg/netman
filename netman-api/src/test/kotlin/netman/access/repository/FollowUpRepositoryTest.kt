package netman.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import java.time.temporal.ChronoUnit
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
        val followUp = FollowUpDTO(
            id = followUpId,
            tenantId = tenant.id!!,
            contactId = contactId,
            status = "OPEN",
            created = Instant.now(),
            note = "Some note",
            followUpTime = Instant.now()
        )

        // Act
        followUpRepository.save(followUp)
        val fetched = followUpRepository.getById(followUpId)
        requireNotNull(fetched)

        // Assert
        assertThat(fetched.id).isEqualTo(followUp.id)
        assertThat(fetched.tenantId).isEqualTo(followUp.tenantId)
        assertThat(fetched.contactId).isEqualTo(followUp.contactId)
        assertThat(fetched.status).isEqualTo(followUp.status)
        // truncating to millis because precision is lost in the database
        assertThat(fetched.created.truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(followUp.created.truncatedTo(ChronoUnit.MILLIS))
        assertThat(fetched.note).isEqualTo(followUp.note)
        assertThat(fetched.followUpTime.truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(followUp.followUpTime.truncatedTo(ChronoUnit.MILLIS))
    }
}
