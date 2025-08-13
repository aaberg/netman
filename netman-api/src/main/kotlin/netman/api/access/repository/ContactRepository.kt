package netman.api.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ContactRepository : GenericRepository<ContactDTO, Long> {

    fun save(contact: ContactDTO) : ContactDTO
    fun update(contact: ContactDTO) : ContactDTO
    fun getById(id: Long): ContactDTO?
    fun findByTenantId(tenantId: Long): List<ContactDTO>
}