package netman.businesslogic

import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.repository.LabelRepository
import netman.businesslogic.models.ContactListItemResource
import netman.businesslogic.models.ContactResource
import netman.businesslogic.models.ContactResourceMapper
import netman.businesslogic.models.FollowUpResource
import netman.businesslogic.models.LabelResource
import netman.businesslogic.models.TenantSummaryResource
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
                taskId = followUp.taskId,
                note = followUp.note,
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
}