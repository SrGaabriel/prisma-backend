package io.github.prismaplatform.database.util

import io.github.prismaplatform.common.dto.*
import io.github.prismaplatform.common.struct.EventRecurrenceType
import io.github.prismaplatform.database.dao.CalendarEvent
import io.github.prismaplatform.database.dao.User

fun User.toUsualDto() = UserDto(
    name
)

fun User.encapsulateWithUsualDto() = UserDtoResponse(toUsualDto())

fun CalendarEvent.toUsualDto() = CalendarEventDto(
    id = id.value,
    name = name,
    description = description,
    location = location,
    creatorId = creator.id.value,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    recurrence = (if (recurrenceType != EventRecurrenceType.NONE) CalendarEventRecurrence(
        type = recurrenceType,
        interval = recurrenceInterval!!,
        daysOfWeek = recurrenceDaysOfWeek?.split(",")?.map { it.toInt() }?.toSet(),
        dayOfMonth = recurrenceDayOfMonth,
        month = recurrenceMonth?.value
    ) else null)
)

fun CalendarEvent.encapsulateWithUsualDto() = CalendarEventResponseCapsule(
    event = toUsualDto()
)