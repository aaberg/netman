package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ContactRepository : GenericRepository<ContactDTO, Long> {
    fun save(contact: ContactDTO) : ContactDTO
    fun update(contact: ContactDTO) : ContactDTO
    fun getById(id: UUID): ContactDTO?
    fun existsById(id: UUID): Boolean
    fun findByTenantId(tenantId: Long): List<ContactDTO>
}