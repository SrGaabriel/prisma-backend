package io.github.prismaplatform.database.dao

import io.github.prismaplatform.common.struct.EventRecurrenceType
import io.github.prismaplatform.database.util.SnowflakeEID
import io.github.prismaplatform.database.util.SnowflakeIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

class Event(id: SnowflakeEID)

object EventTable: SnowflakeIdTable(name="events") {
    val name = varchar("name", 32)
    val description = text("description").nullable().default(null)
    val location = varchar("location", 64)
    val creator = reference("creator", UserTable)
    val startDateTime = datetime("start_date_time")
    val endDateTime = datetime("end_date_time")
    val recurrenceType = enumerationByName("recurrence_type", 16, EventRecurrenceType::class).default(EventRecurrenceType.NONE)
    val recurrenceInterval = integer("recurrence_interval").nullable()
    val recurrenceDaysOfWeek = varchar("recurrence_days_of_week", 14).nullable()
    val recurrenceDayOfMonth = integer("recurrence_day_of_month").nullable()
    val recurrenceMonth = integer("recurrence_month").nullable()
}

