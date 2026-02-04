package netman.businesslogic

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.businesslogic.models.FollowUpTimeSpecification
import netman.businesslogic.models.RegisterFollowUpRequest
import netman.businesslogic.models.TimeSpanType
import netman.models.Frequency
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

@MicronautTest
class TaskManagerUnifiedTest {

    @Inject
    lateinit var taskManager: TaskManager

    @Test
    fun `test unified request with absolute time specification`() {
        val futureTime = Instant.now().plusSeconds(3600) // 1 hour from now
        
        val request = RegisterFollowUpRequest(
            contactId = UUID.randomUUID(),
            note = "Test absolute time follow-up",
            timeSpecification = FollowUpTimeSpecification.Absolute(futureTime),
            frequency = Frequency.Single
        )
        
        // Verify the request can be created and has the correct structure
        assertNotNull(request)
        assertNotNull(request.timeSpecification)
        assert(request.timeSpecification is FollowUpTimeSpecification.Absolute)
    }
    
    @Test
    fun `test unified request with relative time specification`() {
        val request = RegisterFollowUpRequest(
            contactId = UUID.randomUUID(),
            note = "Test relative time follow-up",
            timeSpecification = FollowUpTimeSpecification.Relative(7, TimeSpanType.DAYS),
            frequency = Frequency.Single
        )
        
        // Verify the request can be created and has the correct structure
        assertNotNull(request)
        assertNotNull(request.timeSpecification)
        assert(request.timeSpecification is FollowUpTimeSpecification.Relative)
        
        val relativeSpec = request.timeSpecification as FollowUpTimeSpecification.Relative
        assert(relativeSpec.span == 7)
        assert(relativeSpec.spanType == TimeSpanType.DAYS)
    }
    
    @Test
    fun `test time specification types are properly sealed`() {
        // This test verifies that our sealed class works correctly
        val absoluteSpec = FollowUpTimeSpecification.Absolute(Instant.now())
        val relativeSpec = FollowUpTimeSpecification.Relative(3, TimeSpanType.WEEKS)
        
        assertNotNull(absoluteSpec)
        assertNotNull(relativeSpec)
        
        // Test that when expressions work correctly with the sealed class
        val result1 = when (absoluteSpec) {
            is FollowUpTimeSpecification.Absolute -> "absolute"
            is FollowUpTimeSpecification.Relative -> "relative"
        }
        
        val result2 = when (relativeSpec) {
            is FollowUpTimeSpecification.Absolute -> "absolute"
            is FollowUpTimeSpecification.Relative -> "relative"
        }
        
        assert(result1 == "absolute")
        assert(result2 == "relative")
    }
}