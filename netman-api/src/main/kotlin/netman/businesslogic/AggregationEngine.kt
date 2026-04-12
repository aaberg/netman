package netman.businesslogic

import jakarta.inject.Singleton
import netman.access.FileAccess
import netman.businesslogic.models.ContactFollowUpStatus
import netman.businesslogic.models.ContactListItemResource
import netman.models.Contact
import netman.models.ContactImage
import netman.models.FollowUp
import netman.models.Location
import netman.models.WorkInfo
import java.time.temporal.ChronoUnit

@Singleton
class AggregationEngine(
    private val time: TimeService,
    private val fileAccess: FileAccess
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

            val location = contact.details.filterIsInstance<Location>().firstOrNull()
            val imageRef = contact.details.filterIsInstance<ContactImage>().firstOrNull()

            val imagePublicUrl =
                if (imageRef != null)
                    fileAccess.getFilePublicUrl(imageRef.fileKey)
                else null

            val followUpIn = if (followUp?.followUpTime == null){
                "Not scheduled"
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
                followUpIn,
                imagePublicUrl,
                location?.location
            )
        }

        return contactResourceItems
    }
}