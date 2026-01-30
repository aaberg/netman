package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import netman.models.Tenant
import java.time.Instant
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ActionRepository : GenericRepository<ActionDTO, UUID> {
    fun save(action: ActionDTO): ActionDTO
    fun update(action: ActionDTO): ActionDTO
    fun getById(id: UUID): ActionDTO?
    fun findByTenantId(tenantId: Long, pageable: Pageable): Page<ActionDTO>
    fun findByTenantIdAndStatus(tenantId: Long, status: String, pageable: Pageable): Page<ActionDTO>
    fun findByTenantIdAndStatusAndType(tenantId: Long, status: String, type: String, pageable: Pageable): Page<ActionDTO>
    fun findByStatusAndTriggerTimeBefore(status: String, triggerTime: Instant, pageable: Pageable): Page<ActionDTO>
}
