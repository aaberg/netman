package netman.businesslogic

import jakarta.inject.Singleton
import java.time.Instant

@Singleton
open class TimeService {
    open fun now(): Instant = Instant.now()

}
