package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface Contact2Repository : GenericRepository<Contact2DTO, Long> {
    fun save(contact: Contact2DTO) : Contact2DTO
    fun update(contact: Contact2DTO) : Contact2DTO
    fun getById(id: UUID): Contact2DTO?
    fun existsById(id: UUID): Boolean
    fun findByTenantId(tenantId: Long): List<Contact2DTO>
}