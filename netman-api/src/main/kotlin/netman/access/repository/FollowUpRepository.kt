package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.time.Instant
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface FollowUpRepository : GenericRepository<FollowUpDTO, UUID> {
    fun save(followUp: FollowUpDTO): FollowUpDTO
    fun update(followUp: FollowUpDTO): FollowUpDTO
    fun getById(id: UUID): FollowUpDTO?
    fun findByTenantIdAndContactId(tenantId: Long, contactId: UUID): List<FollowUpDTO>
    fun findByTenantIdAndStatus(tenantId: Long, status: String): List<FollowUpDTO>
    fun findByStatusAndFollowUpTimeLessThan(status: String, followUpTime: Instant): List<FollowUpDTO>
}
