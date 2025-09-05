package netman.access.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ProfileRepository : GenericRepository<ProfileDTO, UUID> {
    fun save(profile: ProfileDTO)
    fun update(profile: ProfileDTO)
    fun getByUserId(userId: UUID): ProfileDTO?
}

@MappedEntity(value = "profile")
data class ProfileDTO(
    @field:Id
    val userId: UUID,
    val name: String,
    val initials: String
)