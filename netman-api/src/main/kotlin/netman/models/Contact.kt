package netman.models

import io.micronaut.core.annotation.Introspected

data class Contact(
    val id: Long,
    val name: String,
    val initials: String
)

abstract class CDetail(val type: String)

data class ContactDetail<T : CDetail>(
    val id: Long,
    val detail: T
)

@Introspected
data class Email(
    val address: String,
    val isPrimary: Boolean,
    val label: String,
) : CDetail("email")

@Introspected
data class Phone(
    val number: String,
    val label: String
) : CDetail("phone")

@Introspected
data class Notes(
    val note: String
) : CDetail("notes")

@Introspected
data class WorkInfo (
    val jobTitle: String,
    val department: String,
    val company: String
) : CDetail("work")