package netman.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.micronaut.serde.annotation.Serdeable
import netman.businesslogic.helper.InitialsGenerator

data class Contact(
    val id: Long? = null,
    val name: String,
    val initials: String
)

inline fun newContact(name: String) : Contact {
    return Contact(name = name, initials = InitialsGenerator.generateInitials(name))
}

@Serdeable
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Email::class, name = "email"),
    JsonSubTypes.Type(value = Phone::class, name = "phone"),
    JsonSubTypes.Type(value = Notes::class, name = "notes"),
    JsonSubTypes.Type(value = WorkInfo::class, name = "work")
)
abstract class CDetail(val type: String)

data class ContactDetail<out T : CDetail>(
    val id: Long? = null,
    val detail: T
)

@Serdeable
data class Email(
    val address: String,
    val isPrimary: Boolean,
    val label: String,
) : CDetail("email")

@Serdeable
data class Phone(
    val number: String,
    val label: String
) : CDetail("phone")

@Serdeable
data class Notes(
    val note: String
) : CDetail("notes")

@Serdeable
data class WorkInfo (
    val jobTitle: String,
    val department: String,
    val company: String
) : CDetail("work")