package netman.access

import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Singleton
import netman.access.repository.*
import netman.models.CDetail
import netman.models.Contact2
import netman.models.Email
import netman.models.Phone
import java.time.Instant
import java.util.*

@Singleton
open class ContactAccess(
    private val contact2Repository: Contact2Repository,
    private val objectMapper: ObjectMapper,
    private val labelRepository: LabelRepository
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
        
        extractAndSaveLabels(contact, tenantId)
        
        return savedContact
    }

    fun getContact(tenantId: Long, contactId: UUID) : Contact2 {
        val contactDto = contact2Repository.getById(contactId)
        requireNotNull(contactDto)
        check(contactDto.tenantId == tenantId)

        return mapContact(contactDto)
    }

    fun listContacts(tenantId: Long) : List<Contact2> {
        return contact2Repository.findByTenantId(tenantId).map { mapContact(it) }
    }

    private fun mapContact(contactDto: Contact2DTO) : Contact2 {
        val contactData = objectMapper.readValue(contactDto.data, ContactData::class.java)
        return Contact2(contactDto.id, contactData.name, contactData.details)
    }
    
    private fun extractAndSaveLabels(contact: Contact2, tenantId: Long) {
        contact.details.forEach { detail ->
            when (detail) {
                is Email -> {
                    if (detail.label.isNotBlank()) {
                        labelRepository.saveLabel(tenantId, detail.label)
                    }
                }
                is Phone -> {
                    if (detail.label.isNotBlank()) {
                        labelRepository.saveLabel(tenantId, detail.label)
                    }
                }
                else -> { /* Other detail types don't have labels */ }
            }
        }
    }
}