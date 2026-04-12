package netman.businesslogic

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.validator.Validator
import jakarta.inject.Singleton
import jakarta.validation.ValidationException
import netman.access.ContactAccess
import netman.access.FileAccess
import netman.access.FileAccessException
import netman.access.repository.LabelRepository
import netman.businesslogic.models.*
import netman.models.CDetail
import netman.models.Contact
import netman.models.ContactImage
import netman.models.Email
import netman.models.Location
import netman.models.Note
import netman.models.Phone
import netman.models.WorkInfo
import java.util.*

@Singleton
class NetworkManager(
    private val contactAccess: ContactAccess,
    private val fileAccess: FileAccess,
    private val imageMimeTypeDetector: ImageMimeTypeDetector,
    private val authorizationEngine: AuthorizationEngine,
    private val validator: Validator,
    private val labelRepository: LabelRepository,
    private val aggregationEngine: AggregationEngine,
    @param:Value("\${fileserver.temp-image.preview-url-duration-seconds:1800}")
    private val tempImagePreviewUrlDurationSeconds: Long,
    private val timeService: TimeService
) {

    fun getMyContacts(userId: String, tenantId: Long): List<ContactListItemResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val contacts = contactAccess.listContacts(tenantId)
        val followUps = contactAccess.getFollowUpsForTenant(tenantId)
        val imageKeyByContactId = contacts
            .mapNotNull { contact ->
                contact.id?.let { id -> id to getContactImageFileKey(contact.details) }
            }
            .toMap()

        return aggregationEngine
            .aggregateAndSummarizeContacts(contacts, followUps)
            .map { contact ->
                val imageUrl = imageKeyByContactId[contact.id]?.let(fileAccess::getFilePublicUrl)
                contact.copy(imageUrl = imageUrl)
            }
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
            workInfo.title, workInfo.organization, note, interactionResources,
            getContactImageFileKey(contact.details)?.let(fileAccess::getFilePublicUrl)
        )
    }

    fun saveContactImage(userId: String, tenantId: Long, contactId: UUID, image: ByteArray) {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val existingContact = contactAccess.getContact(tenantId, contactId)
        val existingImage = existingContact.details.filterIsInstance<ContactImage>().firstOrNull()
        val imageFormat = imageMimeTypeDetector.detectSupportedImageFormat(image)
        val newFileKey = "t${tenantId}-file-$contactId.${imageFormat.extension}"
        val newImageDetail = ContactImage(fileKey = newFileKey, mimeType = imageFormat.mimeType)
        val updatedDetails = existingContact.details.filterNot { it is ContactImage } + newImageDetail
        val updatedContact = existingContact.copy(details = updatedDetails)

        fileAccess.storeFile(newFileKey, image)
        try {
            contactAccess.saveContact(tenantId, updatedContact)
        } catch (e: Exception) {
            fileAccess.deleteFile(newFileKey)
            throw e
        }

        if (existingImage != null && existingImage.fileKey != newFileKey) {
            fileAccess.deleteFile(existingImage.fileKey)
        }
    }

    fun uploadTemporaryContactImage(userId: String, tenantId: Long, image: ByteArray): TemporaryImageUploadResponse {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        val imageFormat = imageMimeTypeDetector.detectSupportedImageFormat(image)
        val temporaryFile = fileAccess.storeTemporaryFile(image)

        val previewUrl = try {
            fileAccess.createTemporaryFilePublicUrl(temporaryFile.tempFileId, tempImagePreviewUrlDurationSeconds)
        } catch (_: FileAccessException) {
            runCatching { fileAccess.deleteTemporaryFile(temporaryFile.tempFileId) }
            throw HttpStatusException(HttpStatus.BAD_GATEWAY, "Failed to generate preview URL")
        }

        return TemporaryImageUploadResponse(
            tempFileId = temporaryFile.tempFileId,
            mimeType = imageFormat.mimeType,
            extension = imageFormat.extension,
            previewUrl = previewUrl,
            previewUrlExpiresAt = timeService.now().plusSeconds(tempImagePreviewUrlDurationSeconds.coerceAtLeast(1))
        )
    }

    fun saveContact(userId: String, tenantId: Long, saveContactRequest: SaveContactRequest)
            : ContactSavedResponse {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)

        val hasIncompleteTempFileMetadata = listOf(
            saveContactRequest.tempFileId,
            saveContactRequest.tempFileMimeType,
            saveContactRequest.tempFileExtension
        ).any { it != null } && listOf(
            saveContactRequest.tempFileId,
            saveContactRequest.tempFileMimeType,
            saveContactRequest.tempFileExtension
        ).any { it == null }
        if (hasIncompleteTempFileMetadata) {
            throw HttpStatusException(HttpStatus.BAD_REQUEST, "Temporary file metadata is incomplete")
        }

        val temporaryFile = saveContactRequest.tempFileId?.let {
            TemporaryFileReference(
                tempFileId = it,
                mimeType = requireNotNull(saveContactRequest.tempFileMimeType),
                extension = requireNotNull(saveContactRequest.tempFileExtension)
            )
        }

        val contactId = saveContactRequest.id ?: UUID.randomUUID()
        val existingContact = saveContactRequest.id?.let { contactAccess.getContact(tenantId, it) }
        val existingImage = existingContact
            ?.details
            ?.filterIsInstance<ContactImage>()
            ?.firstOrNull()

        val email = if (saveContactRequest.email != null)
            Email(saveContactRequest.email, false, "") else null
        val phone = if (saveContactRequest.phone != null)
            Phone(saveContactRequest.phone, "", false) else null
        val note = if (saveContactRequest.notes != null)
            Note(saveContactRequest.notes) else null
        val workInfo = WorkInfo(saveContactRequest.title ?: "", saveContactRequest.organization ?: "")
        val location = if (saveContactRequest.location != null)
            Location(saveContactRequest.location) else null

        val imageDetail = if (temporaryFile != null) {
            ContactImage(
                fileKey = "t${tenantId}-file-$contactId.${temporaryFile.extension}",
                mimeType = temporaryFile.mimeType
            )
        } else {
            existingImage
        }


        val contact = Contact(
            id = contactId,
            name = saveContactRequest.name,
            details = listOfNotNull(email, phone, note, workInfo, location, imageDetail)
        )


        val violations = validator.validate(contact)

        if (violations.isNotEmpty()) {
            throw ValidationException(violations.toString())
        }

        if (temporaryFile != null) {
            try {
                fileAccess.promoteTemporaryFile(temporaryFile.tempFileId, imageDetail!!.fileKey)
            } catch (e: FileAccessException) {
                throw when (e.cause) {
                    is net.aabergs.client.privateapi.NotFoundException ->
                        HttpStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired temporary file")
                    else -> HttpStatusException(HttpStatus.BAD_GATEWAY, "Failed to promote temporary file")
                }
            }
        }

        val savedContact = try {
            contactAccess.saveContact(tenantId, contact)
        } catch (e: Exception) {
            if (temporaryFile != null) {
                runCatching { fileAccess.deleteFile(imageDetail!!.fileKey) }
            }
            throw e
        }
        requireNotNull(savedContact.id)

        if (temporaryFile != null) {
            if (existingImage != null && existingImage.fileKey != imageDetail!!.fileKey) {
                fileAccess.deleteFile(existingImage.fileKey)
            }
        }

        return ContactSavedResponse(savedContact.id)
    }
    
    fun getLabels(userId: String, tenantId: Long): List<LabelResource> {
        authorizationEngine.validateAccessToTenantOrThrow(userId, tenantId)
        return labelRepository.getLabels(tenantId)
            .sortedBy { it.label }
            .map { LabelResource(id = it.id, label = it.label, tenantId = it.tenantId) }
    }

    private fun getContactImageFileKey(details: List<CDetail>): String? {
        return details.filterIsInstance<ContactImage>().firstOrNull()?.fileKey
    }

    private data class TemporaryFileReference(
        val tempFileId: String,
        val mimeType: String,
        val extension: String
    )
}
