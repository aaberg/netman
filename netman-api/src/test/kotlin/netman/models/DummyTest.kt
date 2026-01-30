package netman.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class DummyTest {

    @Test
    fun test_parse_datetime() {
        val dateTimeStr = "2026-02-26T06:01:12.652Z"
        val instant = Instant.parse(dateTimeStr)

        assertThat(instant).isNotNull
    }
}