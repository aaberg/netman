package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TriggerRepository : GenericRepository<TriggerDTO, UUID> {
    fun save(trigger: TriggerDTO): TriggerDTO
    fun update(trigger: TriggerDTO): TriggerDTO
    fun getById(id: UUID): TriggerDTO?
    fun existsById(id: UUID): Boolean
    fun findByTargetTaskId(targetTaskId: UUID): List<TriggerDTO>
}
