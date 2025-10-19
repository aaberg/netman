package netman.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import netman.businesslogic.helper.InitialsGenerator

@Introspected
data class Contact(
    val id: Long? = null,
    @param:NotBlank val name: String,
    val initials: String? = null
)

fun newContact(name: String) : Contact {
    return Contact(name = name, initials = InitialsGenerator.generateInitials(name))
}

@Introspected
data class ContactWithDetails(
    @param:Valid val contact: Contact,
    @param:Valid val details: List<ContactDetail<CDetail>>)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Email::class, name = "email"),
    JsonSubTypes.Type(value = Phone::class, name = "phone"),
    JsonSubTypes.Type(value = Note::class, name = "note"),
    JsonSubTypes.Type(value = WorkInfo::class, name = "work")
)
@Serdeable(validate = false) @Introspected
sealed class CDetail

@Serdeable
data class ContactDetail<out T : CDetail>(
    val id: Long? = null,
    val detail: T
)

@Serdeable
data class Email(
    @param:jakarta.validation.constraints.Email @param:NotBlank
    val address: String,
    val isPrimary: Boolean,
    val label: String,
) : CDetail()

@Serdeable
data class Phone(
    @param:NotBlank val number: String,
    val label: String,
    val isPrimary: Boolean = false
) : CDetail()

@Serdeable
data class Note(
    val note: String
) : CDetail()

@Serdeable
data class WorkInfo (
    val jobTitle: String,
    val department: String,
    val company: String
) : CDetail()