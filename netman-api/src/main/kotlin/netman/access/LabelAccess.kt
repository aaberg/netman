package netman.access

import jakarta.inject.Singleton
import netman.access.repository.LabelDTO
import netman.access.repository.LabelRepository
import netman.models.Label

@Singleton
class LabelAccess(
    private val labelRepository: LabelRepository
) {
    
    fun saveLabel(tenantId: Long, label: String) {
        if (!labelRepository.existsByLabelAndTenantId(label, tenantId)) {
            labelRepository.save(LabelDTO(label, tenantId))
        }
    }
    
    fun getLabels(tenantId: Long): List<Label> {
        return labelRepository.findByTenantId(tenantId).map { 
            Label(it.label, it.tenantId) 
        }
    }
    
    fun saveCommonLabels(tenantId: Long) {
        val commonLabels = listOf("Home", "Work")
        commonLabels.forEach { label ->
            saveLabel(tenantId, label)
        }
    }
}
