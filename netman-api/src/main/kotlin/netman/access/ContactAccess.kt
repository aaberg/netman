package netman.access

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.serde.ObjectMapper
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Singleton
import netman.access.repository.*
import netman.models.CDetail
import netman.models.Interaction
import netman.models.InteractionType
import netman.models.Contact
import netman.models.Email
import netman.models.Phone
import java.time.Instant
import java.util.*

@Singleton
open class ContactAccess(
    private val contactRepository: ContactRepository,
    private val objectMapper: ObjectMapper,
    private val labelRepository: LabelRepository,
    private val interactionRepository: InteractionRepository
) {

    @Serdeable
    data class ContactData(
        val name: String,
        val details: List<CDetail>
    )

    fun saveContact(tenantId: Long, contact: Contact) : Contact {
        val contactData = ContactData(contact.name, contact.details)
        val isNewContact = contact.id == null || !contactRepository.existsById(contact.id)
        val contactDto = ContactDTO(
            id =contact.id ?: UUID.randomUUID(),
            tenantId = tenantId,
            data = objectMapper.writeValueAsString(contactData),
            lastUpdated = Instant.now())
        val savedContactDto: ContactDTO
        if (isNewContact) {
            savedContactDto = contactRepository.save(contactDto)
        } else {
            savedContactDto = contactRepository.update(contactDto)
        }
        val savedContact = mapContact(savedContactDto)
        
        extractAndSaveLabels(contact, tenantId)
        
        return savedContact
    }

    fun getContact(tenantId: Long, contactId: UUID) : Contact {
        val contactDto = contactRepository.getById(contactId)
        requireNotNull(contactDto)
        check(contactDto.tenantId == tenantId)

        return mapContact(contactDto)
    }

    fun listContacts(tenantId: Long) : List<Contact> {
        return contactRepository.findByTenantId(tenantId).map { mapContact(it) }
    }

    private fun mapContact(contactDto: ContactDTO) : Contact {
        val contactData = objectMapper.readValue(contactDto.data, ContactData::class.java)
        return Contact(contactDto.id, contactData.name, contactData.details)
    }
    
    private fun extractAndSaveLabels(contact: Contact, tenantId: Long) {
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
    
    fun saveInteraction(interaction: Interaction): Interaction {
        val id = interaction.id ?: UUID.randomUUID()
        val exists = interaction.id != null && interactionRepository.existsById(interaction.id)
        
        val interactionDto = InteractionDTO(
            id = id,
            contactId = interaction.contactId,
            type = interaction.type.name,
            content = interaction.content,
            timestamp = interaction.timestamp,
            metadata = if (interaction.metadata.isEmpty()) null else objectMapper.writeValueAsString(interaction.metadata)
        )

        val savedDto = if (exists) {
            interactionRepository.update(interactionDto)
        } else {
            interactionRepository.save(interactionDto)
        }
        return mapInteraction(savedDto)
    }
    
    fun getInteractions(contactId: UUID, pageable: Pageable): Page<Interaction> {
        return interactionRepository.findByContactIdOrderByTimestampDesc(contactId, pageable).map { mapInteraction(it) }
    }

    fun getInteraction(interactionId: UUID): Interaction? {
        val dto = interactionRepository.getById(interactionId) ?: return null
        return mapInteraction(dto)
    }

    fun deleteInteractions(interactionId: UUID) {
        interactionRepository.deleteById(interactionId)
    }

    private fun mapInteraction(dto: InteractionDTO): Interaction {
        val metadata = if (dto.metadata != null) {
            try {
                val parsedMap = objectMapper.readValue(dto.metadata, Map::class.java)
                // Safe conversion with validation
                parsedMap.entries.associate { (k, v) -> k.toString() to v.toString() }
            } catch (e: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }
        return Interaction(
            id = dto.id,
            contactId = dto.contactId,
            type = InteractionType.valueOf(dto.type),
            content = dto.content,
            timestamp = dto.timestamp,
            metadata = metadata
        )
    }
}