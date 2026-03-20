package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface InteractionRepository : GenericRepository<InteractionDTO, UUID> {
    fun save(interaction: InteractionDTO): InteractionDTO
    fun getById(id: UUID): InteractionDTO?
    fun findByContactIdOrderByTimestampDesc(contactId: UUID): List<InteractionDTO>
    fun deleteById(contactId: UUID)
    fun update(interaction: InteractionDTO): InteractionDTO
    fun existsById(id: UUID): Boolean
}
