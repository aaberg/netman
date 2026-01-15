package netman.access

import jakarta.inject.Singleton
import netman.access.repository.LabelDTO
import netman.access.repository.LabelRepository
import netman.models.Label

@Singleton
class LabelAccess(
    private val labelRepository: LabelRepository
) {
    
    companion object {
        private val COMMON_LABELS = listOf("Home", "Work")
    }
    
    fun saveLabel(tenantId: Long, label: String) {
        if (!labelRepository.existsByLabelAndTenantId(label, tenantId)) {
            labelRepository.save(LabelDTO(id = null, label = label, tenantId = tenantId))
        }
    }
    
    fun getLabels(tenantId: Long): List<Label> {
        return labelRepository.findByTenantId(tenantId).map { 
            Label(id = it.id!!, label = it.label, tenantId = it.tenantId) 
        }
    }
    
    fun saveCommonLabels(tenantId: Long) {
        COMMON_LABELS.forEach { label ->
            saveLabel(tenantId, label)
        }
    }
}
