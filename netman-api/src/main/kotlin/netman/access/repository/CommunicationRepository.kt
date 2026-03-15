package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface CommunicationRepository : GenericRepository<CommunicationDTO, UUID> {
    fun save(communication: CommunicationDTO): CommunicationDTO
    fun getById(id: UUID): CommunicationDTO?
    fun findByContactIdOrderByTimestampDesc(contactId: UUID): List<CommunicationDTO>
    fun deleteById(contactId: UUID)
    fun update(communication: CommunicationDTO): CommunicationDTO
    fun existsById(id: UUID): Boolean
}
