package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface CommunicationRepository : GenericRepository<CommunicationDTO, UUID> {
    fun save(communication: CommunicationDTO): CommunicationDTO
    fun findByContactIdOrderByTimestampDesc(contactId: UUID): List<CommunicationDTO>
}
