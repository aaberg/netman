package netman.businesslogic.models

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant

/**
 * Sealed class representing different ways to specify when a follow-up should be triggered.
 * This allows for both absolute time specification (specific Instant) and relative time specification (span from now).
 */
@Serdeable
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = FollowUpTimeSpecification.Absolute::class, name = "Absolute"),
    JsonSubTypes.Type(value = FollowUpTimeSpecification.Relative::class, name = "Relative")
)
sealed class FollowUpTimeSpecification {
    
    /**
     * Absolute time specification - follow-up triggers at a specific point in time
     */
    @Serdeable
    data class Absolute(val triggerTime: Instant) : FollowUpTimeSpecification()
    
    /**
     * Relative time specification - follow-up triggers after a certain duration from now
     */
    @Serdeable
    data class Relative(val span: Int, val spanType: TimeSpanType) : FollowUpTimeSpecification()
}