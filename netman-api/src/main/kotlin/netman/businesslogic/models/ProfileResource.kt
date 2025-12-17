package netman.businesslogic.models

import io.micronaut.serde.annotation.Serdeable

/**
 * Represents a user profile with basic information.
 *
 * @property name The display name of the user
 * @property initials The initials of the user
 */
@Serdeable
data class ProfileResource(
    val name: String,
    val initials: String
)
