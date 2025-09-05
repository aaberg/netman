package netman.businesslogic.helper

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.api.Assertions.assertEquals

class InitialsGeneratorTest {

    @ParameterizedTest
    @CsvSource(
        "John Doe, JD",
        "Alice Bob Cooper, ABC",
        "Marie-Anne Smith, MS",
        "James, JA",
        "Robert John Smith-Jones, RJS",
        "Jo, JO",
        "J, J"
    )
    fun `should generate correct initials for different names`(fullName: String, expectedInitials: String) {
        val initials = InitialsGenerator.generateInitials(fullName)
        assertEquals(expectedInitials, initials)
    }

}