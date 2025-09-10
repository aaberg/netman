package netman.access

import jakarta.inject.Singleton
import netman.access.repository.ContactDTO
import netman.access.repository.ContactRepository
import netman.api.contacts.models.ContactResource

@Singleton
class ContactAccess(
    private val contactRepository: ContactRepository
) {
    fun createContact(tenantId: Long, name: String) : ContactResource {
        val contactDto = contactRepository.save(ContactDTO(tenantId = tenantId, name = name))
        return ContactResource(contactDto.id!!, contactDto.name)
    }

    fun getContacts(tenantId: Long) : List<ContactResource> {
        val contactDtos = contactRepository.findByTenantId(tenantId)
        return contactDtos.map { dto ->
            ContactResource(dto.id!!, dto.name)
        }
    }
}