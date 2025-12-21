package netman.access

import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Singleton
import netman.access.repository.*
import netman.models.CDetail
import netman.models.Contact2
import netman.models.Contact2ListItem
import java.time.Instant
import java.util.*

@Singleton
open class ContactAccess(
    private val contactRepository: ContactRepository,
    private val contactDetailRepository: ContactDetailRepository,
    private val contact2Repository: Contact2Repository,
    private val viewContactListRepository: ViewContactListRepository,
    private val objectMapper: ObjectMapper

) {

    @Serdeable
    data class ContactData(
        val name: String,
        val details: List<CDetail>
    )

    fun saveContact(tenantId: Long, contact: Contact2) : Contact2 {
        val contactData = ContactData(contact.name, contact.details)
        val isNewContact = contact.id == null || !contact2Repository.existsById(contact.id)
        val contactDto = Contact2DTO(
            id =contact.id ?: UUID.randomUUID(),
            tenantId = tenantId,
            data = objectMapper.writeValueAsString(contactData),
            lastUpdated = Instant.now())
        val savedContactDto: Contact2DTO
        if (isNewContact) {
            savedContactDto = contact2Repository.save(contactDto)
        } else {
            savedContactDto = contact2Repository.update(contactDto)
        }
        val savedContact = mapContact(savedContactDto)
        updateContactListViewProjection(savedContact, tenantId)
        return savedContact
    }

    fun getContact(tenantId: Long, contactId: UUID) : Contact2 {
        val contactDto = contact2Repository.getById(contactId)
        requireNotNull(contactDto)
        check(contactDto.tenantId == tenantId)

        return mapContact(contactDto)
    }

    private fun mapContact(contactDto: Contact2DTO) : Contact2 {
        val contactData = objectMapper.readValue(contactDto.data, ContactData::class.java)
        return Contact2(contactDto.id, contactData.name, contactData.details)
    }

    private fun updateContactListViewProjection(contact: Contact2, tenantId: Long) {
        val viewItem = toContactListItemDto(contact, tenantId)
        if (viewContactListRepository.existsByContactId(contact.id!!)) {
            viewContactListRepository.update(viewItem)
        } else {
            viewContactListRepository.save(viewItem)
        }
    }

    fun listContacts(tenantId: Long) : List<Contact2ListItem> {
        return viewContactListRepository
            .getByTenantId(tenantId)
            .map { mapContactListItem(it) }
    }

    private fun mapContactListItem(contactDto: ContactListItemDto) : Contact2ListItem {
        return Contact2ListItem(
            contactDto.contactId,
            contactDto.name,
            contactDto.contactInfo,
            contactDto.contactInfoIcon,
            contactDto.labels,
            contactDto.hasUpdates)
    }


//    fun saveContact(tenantId: Long, contact: Contact) : Contact {
//        val contactDto = if (contact.id == null) {
//            contactRepository.save(ContactDTO(tenantId = tenantId, name = contact.name))
//        } else {
//            contactRepository.update(ContactDTO(id = contact.id, tenantId = tenantId, name = contact.name))
//        }
//
//        return Contact(contactDto.id!!, contactDto.name, InitialsGenerator.generateInitials(contactDto.name))
//    }
//
//    fun getContacts(tenantId: Long) : List<Contact> {
//        val contactDtos = contactRepository.findByTenantId(tenantId)
//        return contactDtos.map { dto ->
//            Contact(dto.id!!, dto.name, InitialsGenerator.generateInitials(dto.name))
//        }
//    }
//
//    fun getContact(contactId: Long) : Contact? {
//        return contactRepository.getById(contactId)?.let {
//            Contact(it.id!!, it.name, InitialsGenerator.generateInitials(it.name)) }
//    }
//
//    fun getContactDetails(contactId: Long) : List<ContactDetail<CDetail>> {
//        val contactDetailDtos = contactDetailRepository.findByContactId(contactId)
//        return contactDetailDtos.map { dto -> mapContactDetail(dto) }
//    }
//
//    @Transactional
//    open fun saveDetails(contactId: Long, details: List<ContactDetail<CDetail>>) : List<ContactDetail<CDetail>> {
//        return details.map { detail ->
//            val serializedDetail = objectMapper.writeValueAsString(detail.detail)
//            val contactDetailDto = ContactDetailDTO(id = detail.id, contactId = contactId, detail = serializedDetail)
//            val returnVal: ContactDetailDTO;
//            if (contactDetailDto.id == null) {
//                returnVal = contactDetailRepository.save(contactDetailDto)
//            } else {
//                returnVal = contactDetailRepository.update(contactDetailDto)
//            }
//            mapContactDetail(returnVal)
//        }
//    }
//
//    private fun mapContactDetail(dto: ContactDetailDTO) : ContactDetail<CDetail> {
//        val detail = objectMapper.readValue(dto.detail, CDetail::class.java)
//        return ContactDetail(dto.id, detail)
//    }
}