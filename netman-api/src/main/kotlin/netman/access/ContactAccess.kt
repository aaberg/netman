package netman.access

import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Singleton
import netman.access.repository.*
import netman.models.CDetail
import netman.models.Communication
import netman.models.CommunicationType
import netman.models.Contact2
import netman.models.Email
import netman.models.Phone
import java.time.Instant
import java.util.*

@Singleton
open class ContactAccess(
    private val contact2Repository: Contact2Repository,
    private val objectMapper: ObjectMapper,
    private val labelRepository: LabelRepository,
    private val communicationRepository: CommunicationRepository
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
    
    fun saveCommunication(communication: Communication): Communication {
        val communicationDto = CommunicationDTO(
            id = communication.id ?: UUID.randomUUID(),
            contactId = communication.contactId,
            type = communication.type.name,
            content = communication.content,
            timestamp = communication.timestamp,
            metadata = if (communication.metadata.isEmpty()) null else objectMapper.writeValueAsString(communication.metadata)
        )
        val savedDto = communicationRepository.save(communicationDto)
        return mapCommunication(savedDto)
    }
    
    fun getCommunications(contactId: UUID): List<Communication> {
        return communicationRepository.findByContactId(contactId).map { mapCommunication(it) }
    }
    
    private fun mapCommunication(dto: CommunicationDTO): Communication {
        val metadata = if (dto.metadata != null) {
            objectMapper.readValue(dto.metadata, Map::class.java) as Map<String, String>
        } else {
            emptyMap()
        }
        return Communication(
            id = dto.id,
            contactId = dto.contactId,
            type = CommunicationType.valueOf(dto.type),
            content = dto.content,
            timestamp = dto.timestamp,
            metadata = metadata
        )
    }
}