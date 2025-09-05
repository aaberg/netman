package netman.access.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TenantRepository : GenericRepository<TenantDTO, Long> {

    fun save(tenant: TenantDTO): TenantDTO
    fun update(tenant: TenantDTO): TenantDTO
    fun getById(id: Long): TenantDTO?

    // Finds all tenants that a given user is a member of (join with tenant_member)
    @Query(
        value = """
            SELECT t.id, t.name, t.type
            FROM tenant t
            JOIN tenant_member tm ON tm.tenant_id = t.id
            WHERE tm.user_id = :userId
        """
    )
    fun findAllByUserId(userId: String): List<TenantDTO>

    // Adds a member to a tenant (insert into tenant_member)
    @Query(
        value = """
            INSERT INTO tenant_member (user_id, tenant_id, role)
            VALUES (:userId, :tenantId, :role)
        """
    )
    fun addMemberToTenant(userId: String, tenantId: Long, role: String)

    // Finds all tenant memberships for a given user
    @Query(
        value = """
            SELECT user_id, tenant_id, role
            FROM tenant_member
            WHERE user_id = :userId
        """
    )
    fun findTenantMembersByUserId(userId: String): List<TenantMemberDTO>

}