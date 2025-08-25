package netman.api.access

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import netman.api.access.repository.ContactDTO
import netman.api.access.repository.ContactRepository
import netman.api.access.repository.TenantDTO
import netman.api.access.repository.TenantRepository
import netman.api.models.MemberTenant
import netman.api.models.Tenant
import netman.api.models.TenantRole
import netman.api.models.TenantType
import netman.api.v1.contacts.models.Contact

@Singleton
open class TenantAccess(
    private val tenantRepository: TenantRepository,
    private val contactRepository: ContactRepository,) {

    @Transactional
    open fun registerNewTenant(name: String, type: TenantType, ownerUserId: String) : Tenant {
        val tenantDto = tenantRepository.save(TenantDTO(name = name, type = type.toString()))

        if (tenantDto.id == null) {
            throw IllegalStateException("Failed to register tenant. TenantRepository.create() returned id null")
        }

        tenantRepository.addMemberToTenant(ownerUserId, tenantDto.id, TenantRole.Owner.toString())

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

    fun createContact(tenantId: Long, name: String) : Contact {
        val contactDto = contactRepository.save(ContactDTO(tenantId = tenantId, name = name))
        return Contact(contactDto.id!!, contactDto.name)
    }

    fun getContacts(tenantId: Long) : List<Contact> {
        val contactDtos = contactRepository.findByTenantId(tenantId)
        return contactDtos.map { dto ->
            Contact(dto.id!!, dto.name)
        }
    }
}