package netman.businesslogic

import jakarta.inject.Singleton
import netman.access.ContactAccess
import netman.businesslogic.models.TimeSpanType
import netman.models.FollowUpStatus
import java.time.Instant
import java.time.ZoneId

/**
 * Manager for task-related operations including scheduled follow-ups and action processing.
 */
@Singleton
class TaskManager(
    private val contactAccess: ContactAccess,
    private val timeService: TimeService
) {

    fun runPendingFollowUps() {
        val followUps = contactAccess.getAllDueFollowUps()

        followUps.forEach { followUp ->
            val dueFollowUp = followUp.copy(status = FollowUpStatus.Due)
            contactAccess.markFollowUpAsDue(dueFollowUp)
        }
    }

    private fun calculateTriggerTimeFromSpan(span: Int, spanType: TimeSpanType): Instant {
        val now = timeService.now()
        val zonedDateTime = now.atZone(ZoneId.of("UTC"))
        
        val resultTime = when (spanType) {
            TimeSpanType.DAYS -> zonedDateTime.plusDays(span.toLong())
            TimeSpanType.WEEKS -> zonedDateTime.plusWeeks(span.toLong())
            TimeSpanType.MONTHS -> zonedDateTime.plusMonths(span.toLong())
            TimeSpanType.YEARS -> zonedDateTime.plusYears(span.toLong())
        }
        
        return resultTime.toInstant()
    }
}