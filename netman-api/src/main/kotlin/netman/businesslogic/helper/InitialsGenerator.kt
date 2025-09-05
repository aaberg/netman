package netman.businesslogic.helper

object InitialsGenerator {
    
    // Creates two or three-letter initials from a name.
    fun generateInitials(name: String): String {
        if (name.isBlank()) return ""

        val parts = name.trim().split("\\s+".toRegex())
        return when {
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> parts.take(3)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
        }
    }
}