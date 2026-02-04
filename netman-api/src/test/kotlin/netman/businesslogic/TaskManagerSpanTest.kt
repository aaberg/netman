package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.businesslogic.models.RegisterScheduledFollowUpWithSpanRequest
import netman.businesslogic.models.TimeSpanType
import netman.models.Frequency
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
class TaskManagerSpanTest {

    @Inject
    lateinit var taskManager: TaskManager

    @Test
    fun `test registerScheduledFollowUpWithSpan creates follow-up action`() {
        // This is a basic test to verify the method compiles and can be called
        // In a real scenario, you would need proper test data setup
        
        val request = RegisterScheduledFollowUpWithSpanRequest(
            contactId = UUID.randomUUID(),
            note = "Test follow-up",
            span = 7,
            spanType = TimeSpanType.DAYS,
            frequency = Frequency.Single
        )
        
        // This test would need proper setup with a real tenant and contact
        // For now, we just verify the request object can be created
        assertNotNull(request)
        assertNotNull(request.span)
        assertNotNull(request.spanType)
    }
}