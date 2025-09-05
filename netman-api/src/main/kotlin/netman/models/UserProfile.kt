package netman.models

import io.micronaut.core.annotation.Introspected

@Introspected
data class UserProfile(
    val name: String,
    val initials: String
    )
