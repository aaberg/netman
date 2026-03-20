package netman.businesslogic

import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.repository.LabelRepository
import netman.businesslogic.models.*
import netman.models.Contact
import netman.models.Email
import netman.models.Note
import netman.models.Phone
import netman.models.WorkInfo
import java.util.*

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator,
    private val labelRepository: LabelRepository,
    private val aggregationEngine: AggregationEngine
) {

    fun getMyContacts(userId: String, tenantId: Long): List<ContactListItemResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val contacts = contactAccess.listContacts(tenantId)
        val followUps = contactAccess.getFollowUpsForTenant(tenantId)
        return aggregationEngine.aggregateAndSummarizeContacts(contacts, followUps);
    }

    fun getContactDetails(userId: String, tenantId: Long, contactId: UUID): ContactDetailsResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val contact = contactAccess.getContact(tenantId, contactId)
        requireNotNull(contact.id)

        val interactions = contactAccess.getInteractions(contactId)
        val interactionResources = interactions.map { i ->
            InteractionResource(i.id, i.contactId, i.type, i.content, i.timestamp, i.metadata)
        }

        val email = contact.details.filterIsInstance<Email>().firstOrNull()?.address ?: ""
        val phone = contact.details.filterIsInstance<Phone>().firstOrNull()?.number ?: ""
        val workInfo = contact.details.filterIsInstance<WorkInfo>().firstOrNull() ?: WorkInfo.empty
        val note = contact.details.filterIsInstance<Note>().firstOrNull()?.note ?: ""

        return ContactDetailsResource(
            contact.id, contact.name, contact.initials, email, phone,
            workInfo.title, workInfo.organization, note, interactionResources
        )
    }

    fun saveContact(userId: String, tenantId: Long, saveContactRequest: SaveContactRequest)
            : ContactSavedResponse {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val email = if (saveContactRequest.email != null)
            Email(saveContactRequest.email, false, "") else null
        val phone = if (saveContactRequest.phone != null)
            Phone(saveContactRequest.phone, "", false) else null
        val note = if (saveContactRequest.notes != null)
            Note(saveContactRequest.notes) else null
        val workInfo = WorkInfo(saveContactRequest.title ?: "", saveContactRequest.organization ?: "")


        val contact = Contact(
            id = saveContactRequest.id,
            name = saveContactRequest.name,
            details = listOfNotNull(email, phone, note, workInfo)
        )


        val violations = validator.validate(contact)

        if (violations.isNotEmpty()) {
            throw ValidationException(violations.toString())
        }

        val savedContact = contactAccess.saveContact(tenantId, contact)
        requireNotNull(savedContact.id)
        return ContactSavedResponse(savedContact.id)
    }
    
    fun getLabels(userId: String, tenantId: Long): List<LabelResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return labelRepository.getLabels(tenantId)
            .sortedBy { it.label }
            .map { LabelResource(id = it.id, label = it.label, tenantId = it.tenantId) }
    }
}