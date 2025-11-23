package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ViewContactListRepository : GenericRepository<ContactListItemDto, UUID> {
    fun save(dto: ContactListItemDto): ContactListItemDto
    fun update(dto: ContactListItemDto): ContactListItemDto
    fun getByTenantId(tenantId: Long): List<ContactListItemDto>
    fun existsByContactId(id: UUID): Boolean
}