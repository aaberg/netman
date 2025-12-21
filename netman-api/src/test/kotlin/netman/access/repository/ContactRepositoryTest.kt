package netman.access.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest(startApplication = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContactRepositoryTest : DefaultTestProperties() {

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var tenantRepository: TenantRepository

    @Test
    fun `save and fetch contact by id`() {
        // Arrange
        val tenant = tenantRepository.save(TenantDTO( name ="ten", type = "Organization"))

        // Act
        val contact = contactRepository.save(ContactDTO(tenantId = tenant.id!!, name = "John Doe"))
        val fetched = contactRepository.getById(contact.id!!)

        // Assert
        assertThat(fetched).isEqualTo(contact)
        assertThat(fetched!!.name).isEqualTo("John Doe")
    }
}