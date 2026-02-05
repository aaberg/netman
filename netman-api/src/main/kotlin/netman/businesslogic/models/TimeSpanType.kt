package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable

@Serdeable
enum class TimeSpanType {
    DAYS,
    WEEKS,
    MONTHS,
    YEARS
}