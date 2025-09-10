package netman.access.repository

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType

@MappedEntity("contact_detail")
data class ContactDetailDTO(
    @field:Id
    @field:GeneratedValue
    val id: Long? = null,
    val contactId: Long,
    val type: String,
    @field:TypeDef(type = DataType.JSON)
    val detail: String
)

