package netman.models

import io.micronaut.serde.ObjectMapper
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AssertionFailureBuilder
import org.junit.jupiter.api.Test

@MicronautTest(startApplication = false)
class SerializationTest {

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `email deserialization test`() {
        // Arrange
        val jsonStr = """
            {
                "type": "email",
                "address": "alice@example.com",
                "isPrimary": true,
                "label": "Personal"
            }
        """.trimIndent()

        // Act
        val result = objectMapper.readValue(jsonStr, CDetail::class.java)

        // Assert
        if (result !is Email) {
            throw AssertionFailureBuilder.assertionFailure()
                .message("result was expected to be of type Email, but was ${result.javaClass.name}")
                .build()
        }
        assertThat(result.address).isEqualTo("alice@example.com")
        assertThat(result.isPrimary).isTrue()
        assertThat(result.label).isEqualTo("Personal")
    }

    @Test
    fun `phone deserialization test`() {
        // Arrange
        val jsonStr = """
            {
                "type": "phone",
                "number": "+1234567890",
                "isPrimary": true,
                "label": "Work"
            }
        """.trimIndent()

        // Act
        val result = objectMapper.readValue(jsonStr, CDetail::class.java)

        // Assert
        if (result !is Phone) {
            throw AssertionFailureBuilder.assertionFailure()
                .message("result was expected to be of type Phone, but was ${result.javaClass.name}")
                .build()
        }
        assertThat(result.number).isEqualTo("+1234567890")
        assertThat(result.isPrimary).isTrue()
        assertThat(result.label).isEqualTo("Work")
    }

    @Test
    fun `note deserialization test`() {
        // Arrange
        val jsonStr = """
            {
                "type": "note",
                "note": "A note"
            }
        """.trimIndent()

        // Act
        val result = objectMapper.readValue(jsonStr, CDetail::class.java)

        // Assert
        if (result !is Note) {
            throw AssertionFailureBuilder.assertionFailure()
                .message("result was expected to be of type Note, but was ${result.javaClass.name}")
                .build()
        }
        assertThat(result.note).isEqualTo("A note")
    }
}