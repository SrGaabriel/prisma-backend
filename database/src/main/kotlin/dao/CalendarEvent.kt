package io.github.prismaplatform.database.dao

import io.github.prismaplatform.common.struct.EventRecurrenceType
import io.github.prismaplatform.database.util.SnowflakeEID
import io.github.prismaplatform.database.util.SnowflakeEntity
import io.github.prismaplatform.database.util.SnowflakeEntityClass
import io.github.prismaplatform.database.util.SnowflakeIdTable
import kotlinx.datetime.Month
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone

class CalendarEvent(id: SnowflakeEID): SnowflakeEntity(id) {
    companion object : SnowflakeEntityClass<CalendarEvent>(CalendarEventTable)

    var name by CalendarEventTable.name
    var description by CalendarEventTable.description
    var location by CalendarEventTable.location
    var creator by User referencedOn CalendarEventTable.creator
    var timezoneId by CalendarEventTable.timezoneId
    var startDateTime by CalendarEventTable.startDateTime
    var endDateTime by CalendarEventTable.endDateTime
    var recurrenceType by CalendarEventTable.recurrenceType
    var recurrenceInterval by CalendarEventTable.recurrenceInterval
    var recurrenceDaysOfWeek by CalendarEventTable.recurrenceDaysOfWeek
    var recurrenceDayOfMonth by CalendarEventTable.recurrenceDayOfMonth
    var recurrenceMonth by CalendarEventTable.recurrenceMonth
}

object CalendarEventTable: SnowflakeIdTable(name="events") {
    val name = varchar("name", 32)
    val description = text("description").nullable().default(null)
    val location = varchar("location", 64).nullable()
    val creator = reference("creator", UserTable)
    val timezoneId = varchar("timezone_id", 32)
    val startDateTime = datetime("start_date_time")
    val endDateTime = datetime("end_date_time")
    val recurrenceType = enumerationByName("recurrence_type", 16, EventRecurrenceType::class).default(EventRecurrenceType.NONE)
    val recurrenceInterval = integer("recurrence_interval").nullable()
    val recurrenceDaysOfWeek = varchar("recurrence_days_of_week", 14).nullable()
    val recurrenceDayOfMonth = integer("recurrence_day_of_month").nullable()
    val recurrenceMonth = enumerationByName("recurrence_month", 9, Month::class).nullable()
}

