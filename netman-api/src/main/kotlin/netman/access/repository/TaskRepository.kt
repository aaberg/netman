package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TaskRepository : GenericRepository<TaskDTO, UUID> {
    fun save(task: TaskDTO): TaskDTO
    fun update(task: TaskDTO): TaskDTO
    fun getById(id: UUID): TaskDTO?
    fun existsById(id: UUID): Boolean
    fun findByUserId(userId: UUID): List<TaskDTO>
    fun findByUserIdAndTenantId(userId: UUID, tenantId: Long): List<TaskDTO>
    fun findByUserIdAndTenantIdIn(userId: UUID, tenantIds: List<Long>): List<TaskDTO>
}
