package netman.api.access.repository

import io.micronaut.data.repository.CrudRepository
import jakarta.inject.Singleton

@Singleton
abstract class ContactRepository : CrudRepository<ContactDto, Long> {


}