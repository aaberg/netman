package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface InteractionRepository : GenericRepository<InteractionDTO, UUID> {
    fun save(communication: InteractionDTO): InteractionDTO
    fun getById(id: UUID): InteractionDTO?
    fun findByContactIdOrderByTimestampDesc(contactId: UUID, pageable: Pageable): Page<InteractionDTO>
    fun deleteById(contactId: UUID)
    fun update(communication: InteractionDTO): InteractionDTO
    fun existsById(id: UUID): Boolean
}
