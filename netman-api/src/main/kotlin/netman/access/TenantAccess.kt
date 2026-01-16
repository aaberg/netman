package netman.access

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import netman.access.repository.LabelRepository
import netman.access.repository.TenantDTO
import netman.access.repository.TenantRepository
import netman.models.MemberTenant
import netman.models.Tenant
import netman.models.TenantRole
import netman.models.TenantType

@Singleton
open class TenantAccess(
    private val tenantRepository: TenantRepository,
    private val labelRepository: LabelRepository
) {

    @Transactional
    open fun registerNewTenant(name: String, type: TenantType, ownerUserId: String) : Tenant {
        val tenantDto = tenantRepository.save(TenantDTO(name = name, type = type.toString()))

        if (tenantDto.id == null) {
            throw IllegalStateException("Failed to register tenant. TenantRepository.create() returned id null")
        }

        tenantRepository.addMemberToTenant(ownerUserId, tenantDto.id, TenantRole.Owner.toString())
        
        labelRepository.saveCommonLabels(tenantDto.id)

        return Tenant(tenantDto.id, tenantDto.name, TenantType.valueOf(tenantDto.type))
    }

    fun associateMemberToTenant(userId: String, tenant: Tenant, role: TenantRole) = tenantRepository.addMemberToTenant(
        userId, tenant.id, role.toString()
    )

    fun getMemberTenants(userId: String) : List<MemberTenant>  {
        val tenantMembers = tenantRepository.findTenantMembersByUserId(userId)

        val tenantDtos = tenantRepository.findAllByUserId(userId)

        return tenantDtos.map {(id, name, type) ->
            val type = TenantType.valueOf(type)
            val role = TenantRole.valueOf(tenantMembers.single{ tm -> tm.tenantId == id }.role)
            MemberTenant(Tenant(id!!, name, type), userId, role)
        }
    }
}