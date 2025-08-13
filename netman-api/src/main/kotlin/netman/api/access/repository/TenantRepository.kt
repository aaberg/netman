package netman.api.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.*

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TenantRepository : GenericRepository<TenantDTO, Long> {

    fun save(tenant: TenantDTO) : TenantDTO
    fun update(tenant: TenantDTO) : TenantDTO
    fun getById(id: Long): Optional<TenantDTO>
}