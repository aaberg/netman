package netman.businesslogic

import jakarta.inject.Singleton
import netman.businesslogic.models.ContactFollowUpStatus
import netman.businesslogic.models.ContactListItemResource
import netman.models.Contact
import netman.models.FollowUp
import netman.models.WorkInfo
import java.time.temporal.ChronoUnit

@Singleton
class AggregationEngine(
    private val time: TimeService
) {
    fun aggregateAndSummarizeContacts(
        contacts: List<Contact>,
        followUps: List<FollowUp>) : List<ContactListItemResource> {

        val followUpMap = followUps.sortedByDescending { it.created }.associateBy { it.contactId }

        val contactResourceItems = contacts.map { contact ->

            val workInfo = contact.details.filterIsInstance<WorkInfo>().firstOrNull()
            val followUp = followUpMap[contact.id]
            val contactFollowUpStatus = if (followUp != null && followUp.followUpTime > time.now()) {
                ContactFollowUpStatus.Scheduled
            } else if (followUp != null && followUp.followUpTime <= time.now()) {
                ContactFollowUpStatus.Overdue
            } else {
                ContactFollowUpStatus.None
            }


            val followUpIn = if (followUp?.followUpTime == null){
                "Connected"
            } else {
                "${time.now().until(followUp.followUpTime, ChronoUnit.DAYS)} days"
            }

            ContactListItemResource(
                contact.id!!,
                contact.name,
                contact.initials,
                workInfo?.title ?: "",
                workInfo?.organization ?: "",
                contactFollowUpStatus,
                followUpIn
            )
        }

        return contactResourceItems
    }
}