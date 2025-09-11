package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ContactDetailRepository : GenericRepository<ContactDetailDTO, Long> {

    fun save(contactDetail: ContactDetailDTO) : ContactDetailDTO
    fun update(contactDetail: ContactDetailDTO) : ContactDetailDTO
    fun findByContactId(contactId: Long): List<ContactDetailDTO>
}