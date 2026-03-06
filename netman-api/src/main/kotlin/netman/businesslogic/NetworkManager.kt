package netman.businesslogic

import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.repository.LabelRepository
import netman.businesslogic.models.*
import netman.models.Communication
import java.util.*

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator,
    private val contactResourceMapper: ContactResourceMapper,
    private val labelRepository: LabelRepository,
    private val actionAccess: netman.access.ActionAccess
) {

    fun getMyContacts(userId: String, tenantId: Long): List<ContactListItemResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val contacts = contactAccess.listContacts(tenantId)

        return contacts.map { contactResourceMapper.mapToListItem(it) }
    }

    fun getContactWithDetails(userId: String, tenantId: Long, contactId: UUID): ContactResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val contact = contactAccess.getContact(tenantId, contactId)
        return contactResourceMapper.map(contact)
    }

    fun saveContactWithDetails(userId: String, tenantId: Long, contactResource: ContactResource): ContactResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        
        val contact = contactResourceMapper.map(contactResource)
        val violations = validator.validate(contact)

        if (violations.isNotEmpty()) {
            throw ValidationException(violations.toString())
        }

        val savedContact = contactAccess.saveContact(tenantId, contact)
        requireNotNull(savedContact.id)

        return contactResourceMapper.map(savedContact)
    }
    
    fun getLabels(userId: String, tenantId: Long): List<LabelResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return labelRepository.getLabels(tenantId)
            .sortedBy { it.label }
            .map { LabelResource(id = it.id, label = it.label, tenantId = it.tenantId) }
    }

    fun summariseTenant(userId: String, tenantId: Long): TenantSummaryResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        
        // Get number of contacts
        val contacts = contactAccess.listContacts(tenantId)
        val numberOfContacts = contacts.size
        
        // Get number of pending actions (use small page size and totalSize for count)
        val pendingActions = actionAccess.getActions(
            tenantId,
            netman.models.ActionStatus.Pending,
            null,
            io.micronaut.data.model.Pageable.from(0, 1)
        )
        val numberOfPendingActions = pendingActions.totalSize.toInt()
        
        // Get pending follow-ups (limit to first N items for summary)
        val pendingFollowUps = actionAccess.getFollowUps(
            tenantId,
            netman.models.FollowUpStatus.Pending,
            io.micronaut.data.model.Pageable.from(0, 10)
        )
        
        // Map follow-ups to FollowUpResource
        val followUpResources = pendingFollowUps.content.map { followUp ->
            FollowUpResource(
                id = followUp.id,
                contactId = followUp.contactId,
                contactName = contacts.find { it.id == followUp.contactId }?.name ?: "Unknown",
                taskId = followUp.taskId,
                note = followUp.note,
                status = followUp.status,
                created = followUp.created
            )
        }
        
        return TenantSummaryResource(
            tenantId = tenantId,
            numberOfContacts = numberOfContacts,
            numberOfPendingActions = numberOfPendingActions,
            pendingFollowUps = followUpResources
        )
    }
    
    fun saveCommunication(userId: String, tenantId: Long, communicationResource: CommunicationResource): CommunicationResource {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        
        // Verify the contact belongs to this tenant
        val contact = contactAccess.getContact(tenantId, communicationResource.contactId)
        requireNotNull(contact.id)
        
        val communication = Communication(
            id = communicationResource.id,
            contactId = communicationResource.contactId,
            type = communicationResource.type,
            content = communicationResource.content,
            timestamp = communicationResource.timestamp,
            metadata = communicationResource.metadata
        )
        
        val savedCommunication = contactAccess.saveCommunication(communication)
        
        return CommunicationResource(
            id = savedCommunication.id,
            contactId = savedCommunication.contactId,
            type = savedCommunication.type,
            content = savedCommunication.content,
            timestamp = savedCommunication.timestamp,
            metadata = savedCommunication.metadata
        )
    }
    
    fun getCommunications(userId: String, tenantId: Long, contactId: UUID): List<CommunicationResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val communications = contactAccess.getCommunications(contactId)
        
        return communications.map { communication ->
            CommunicationResource(
                id = communication.id,
                contactId = communication.contactId,
                type = communication.type,
                content = communication.content,
                timestamp = communication.timestamp,
                metadata = communication.metadata
            )
        }
    }
}