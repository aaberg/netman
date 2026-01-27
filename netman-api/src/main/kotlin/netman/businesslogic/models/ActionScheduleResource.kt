package netman.businesslogic.models

import netman.models.Frequency
import java.time.Instant

data class ActionScheduleResource(
    val triggerTime: Instant,
    val frequency: Frequency,
)
