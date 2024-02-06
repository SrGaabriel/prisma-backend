package io.github.prismaplatform.common.dto

import io.github.prismaplatform.common.Snowflake
import io.github.prismaplatform.common.struct.EventRecurrenceType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEventDto(
    val id: Snowflake,
    val name: String,
    val description: String?,
    val location: String?,
    val creatorId: Snowflake,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val recurrence: CalendarEventRecurrence? = null
)

@Serializable
data class CalendarEventRecurrence(
    val type: EventRecurrenceType,
    val interval: Int,
    val daysOfWeek: Set<Int>? = null,
    val dayOfMonth: Int? = null,
    val month: Int? = null
)

@Serializable
data class CalendarEventScheduleResponse(
    val name: String,
    val description: String? = null,
    val location: String? = null,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val timezoneId: String,
    val recurrence: CalendarEventRecurrence? = null
)

@Serializable
data class CalendarEventResponseCapsule(
    val event: CalendarEventDto
)

@Serializable
data class CalendarEventPluralResponseCapsule(
    val events: List<CalendarEventDto>
)