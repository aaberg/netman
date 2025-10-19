package netman.models

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@MicronautTest(startApplication = false)
class ValidationTest {

    @Inject
    lateinit var validator: Validator


    companion object {
        @JvmStatic
        fun emailValidationDataSource(): List<Arguments> = listOf(
            Arguments.of("", "primary", true, 2),
            Arguments.of(" ", "primary", true, 2),
            Arguments.of("john@doe.com", "primary", true, 0),
            Arguments.of("john.doe@gmail.com", "", true, 0),
        )
    }

    @ParameterizedTest
    @MethodSource("emailValidationDataSource")
    fun `email validation tests`(address: String, label: String, isPrimary: Boolean, expectedNumberOfErrors: Int) {
        val email = Email(address, isPrimary, label)
        val result = validator.validate(email)
        assertThat(result.size).isEqualTo(expectedNumberOfErrors)
    }
}