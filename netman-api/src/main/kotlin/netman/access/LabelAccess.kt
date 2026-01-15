package netman.access

import jakarta.inject.Singleton
import netman.access.repository.LabelDTO
import netman.access.repository.LabelId
import netman.access.repository.LabelRepository
import netman.models.Label

@Singleton
class LabelAccess(
    private val labelRepository: LabelRepository
) {
    
    fun saveLabel(tenantId: Long, label: String) {
        val labelId = LabelId(label, tenantId)
        if (!labelRepository.existsById(labelId)) {
            labelRepository.save(LabelDTO(labelId))
        }
    }
    
    fun getLabels(tenantId: Long): List<Label> {
        return labelRepository.findByIdTenantId(tenantId).map { 
            Label(it.id.label, it.id.tenantId) 
        }
    }
    
    fun saveCommonLabels(tenantId: Long) {
        val commonLabels = listOf("Home", "Work")
        commonLabels.forEach { label ->
            saveLabel(tenantId, label)
        }
    }
}
