package netman.access

import io.micronaut.serde.ObjectMapper
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import netman.access.repository.ContactDTO
import netman.access.repository.ContactDetailDTO
import netman.access.repository.ContactDetailRepository
import netman.access.repository.ContactRepository
import netman.businesslogic.helper.InitialsGenerator
import netman.models.CDetail
import netman.models.Contact
import netman.models.ContactDetail

@Singleton
open class ContactAccess(
    private val contactRepository: ContactRepository,
    private val contactDetailRepository: ContactDetailRepository,
    private val objectMapper: ObjectMapper
) {
    fun saveContact(tenantId: Long, contact: Contact) : Contact {
        val contactDto = contactRepository.save(ContactDTO(id = contact.id, tenantId = tenantId, name = contact.name))
        return Contact(contactDto.id!!, contactDto.name, InitialsGenerator.generateInitials(contactDto.name))
    }

    fun getContacts(tenantId: Long) : List<Contact> {
        val contactDtos = contactRepository.findByTenantId(tenantId)
        return contactDtos.map { dto ->
            Contact(dto.id!!, dto.name, InitialsGenerator.generateInitials(dto.name))
        }
    }

    fun getContact(contactId: Long) : Contact? {
        return contactRepository.getById(contactId)?.let {
            Contact(it.id!!, it.name, InitialsGenerator.generateInitials(it.name)) }
    }

    fun getContactDetails(contactId: Long) : List<ContactDetail<CDetail>> {
        val contactDetailDtos = contactDetailRepository.findByContactId(contactId)
        return contactDetailDtos.map { dto -> mapContactDetail(dto) }
    }

    @Transactional
    open fun saveDetails(contactId: Long, details: List<ContactDetail<CDetail>>) : List<ContactDetail<CDetail>> {
        return details.map { detail ->
            val serializedDetail = objectMapper.writeValueAsString(detail.detail)
            val contactDetailDto = ContactDetailDTO(id = detail.id, contactId = contactId, type = detail.detail.type, detail = serializedDetail)
            val returnVal: ContactDetailDTO;
            if (contactDetailDto.id == null) {
                returnVal = contactDetailRepository.save(contactDetailDto)
            } else {
                returnVal = contactDetailRepository.update(contactDetailDto)
            }
            mapContactDetail(returnVal)
        }
    }

    private fun mapContactDetail(dto: ContactDetailDTO) : ContactDetail<CDetail> {
        val detail = objectMapper.readValue(dto.detail, CDetail::class.java)
        return ContactDetail(dto.id, detail)
    }
}