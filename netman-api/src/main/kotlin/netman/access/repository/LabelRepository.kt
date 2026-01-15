package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface LabelRepository : GenericRepository<LabelDTO, String> {
    fun save(dto: LabelDTO): LabelDTO
    fun findByTenantId(tenantId: Long): List<LabelDTO>
    fun existsByLabelAndTenantId(label: String, tenantId: Long): Boolean
}
