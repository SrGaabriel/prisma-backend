package io.github.prismaplatform.common.struct

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime

abstract class EventRecurrence(val interval: Int, val period: DatePeriod) {
    abstract fun isRecurringOn(date: LocalDateTime): Boolean

    class Daily(interval: Int): EventRecurrence(interval, DatePeriod.DAY) {
        override fun isRecurringOn(date: LocalDateTime): Boolean = true
    }

    class Weekly(interval: Int, val daysOfWeek: List<DayOfWeek>): EventRecurrence(interval, DatePeriod.WEEK) {
        override fun isRecurringOn(date: LocalDateTime): Boolean {
            return daysOfWeek.contains(date.dayOfWeek)
        }
    }

    class Monthly(interval: Int, val dayOfMonth: Int): EventRecurrence(interval, DatePeriod.MONTH) {
        override fun isRecurringOn(date: LocalDateTime): Boolean {
            return date.dayOfMonth == dayOfMonth
        }
    }

    class Yearly(interval: Int, val month: Int, val dayOfMonth: Int): EventRecurrence(interval, DatePeriod.YEAR) {
        override fun isRecurringOn(date: LocalDateTime): Boolean {
            return date.monthNumber == month && date.dayOfMonth == dayOfMonth
        }
    }
}

enum class EventRecurrenceType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY;

    fun toEventRecurrence(interval: Int, daysOfWeek: List<DayOfWeek>?, dayOfMonth: Int?, month: Int?): EventRecurrence {
        return when (this) {
            NONE -> throw IllegalArgumentException("EventRecurrenceType.NONE cannot be converted to EventRecurrence")
            DAILY -> EventRecurrence.Daily(interval)
            WEEKLY -> EventRecurrence.Weekly(interval, daysOfWeek ?: throw IllegalArgumentException("EventRecurrenceType.WEEKLY requires daysOfWeek"))
            MONTHLY -> EventRecurrence.Monthly(interval, dayOfMonth ?: throw IllegalArgumentException("EventRecurrenceType.MONTHLY requires dayOfMonth"))
            YEARLY -> EventRecurrence.Yearly(interval, month ?: throw IllegalArgumentException("EventRecurrenceType.YEARLY requires month"), dayOfMonth ?: throw IllegalArgumentException("EventRecurrenceType.YEARLY requires dayOfMonth"))
        }
    }
}

enum class DatePeriod {
    DAY, WEEK, MONTH, YEAR
}
