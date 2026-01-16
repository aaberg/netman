package netman.access.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import netman.models.Label
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface LabelRepository : GenericRepository<LabelDTO, UUID> {
    fun save(dto: LabelDTO): LabelDTO
    fun findByTenantId(tenantId: Long): List<LabelDTO>
    fun existsByLabelAndTenantId(label: String, tenantId: Long): Boolean
    
    companion object {
        private val COMMON_LABELS = listOf("Home", "Work")
    }
    
    fun saveLabel(tenantId: Long, label: String) {
        if (!existsByLabelAndTenantId(label, tenantId)) {
            save(LabelDTO(id = null, label = label, tenantId = tenantId))
        }
    }
    
    fun getLabels(tenantId: Long): List<Label> {
        return findByTenantId(tenantId).map { 
            Label(id = it.id!!, label = it.label, tenantId = it.tenantId) 
        }
    }
    
    fun saveCommonLabels(tenantId: Long) {
        COMMON_LABELS.forEach { label ->
            saveLabel(tenantId, label)
        }
    }
}
