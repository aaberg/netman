package netman.businesslogic

import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.models.Contact2
import netman.models.Contact2ListItem
import java.util.UUID

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator
) {

    fun getMyContacts(userId: String, tenantId: Long) : List<Contact2ListItem> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        return contactAccess.listContacts(tenantId)
    }

    fun getContactWithDetails(userId: String, tenantId: Long, contactId: UUID) : Contact2 {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return contactAccess.getContact(tenantId, contactId)
    }

    fun saveContactWithDetails(userId: String, tenantId: Long, contact: Contact2) : Contact2 {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val violations = validator.validate(contact )

        if (violations.isNotEmpty()){
            throw ValidationException(violations.toString())
        }

        val savedContact = contactAccess.saveContact(tenantId, contact)
        requireNotNull(savedContact.id)

        return savedContact
    }
}