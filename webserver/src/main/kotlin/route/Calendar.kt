package io.github.prismaplatform.webserver.route

import io.github.prismaplatform.common.SnowflakeService
import io.github.prismaplatform.common.dto.CalendarEventPluralResponseCapsule
import io.github.prismaplatform.common.dto.CalendarEventScheduleResponse
import io.github.prismaplatform.common.struct.EventRecurrenceType
import io.github.prismaplatform.database.dao.CalendarEvent
import io.github.prismaplatform.database.dao.CalendarEventTable
import io.github.prismaplatform.database.util.encapsulateWithUsualDto
import io.github.prismaplatform.database.util.toUsualDto
import io.github.prismaplatform.webserver.auth.authenticate
import io.github.prismaplatform.webserver.util.receiveOrBadRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.ktor.ext.inject
import java.time.Month

fun Route.calendarRoute() {
    val snowflakeService by inject<SnowflakeService>()
    post<RealmResource.Id.Calendar.Events> {
        authenticate { user ->
            val data = call.receiveOrBadRequest<CalendarEventScheduleResponse>() ?: return@post

            val createdEvent = newSuspendedTransaction {
                CalendarEvent.new(snowflakeService.generate()) {
                    name = data.name
                    description = data.description
                    location = data.location
                    creator = user
                    startDateTime = data.startDateTime
                    endDateTime = data.endDateTime
                    timezoneId = data.timezoneId
                    recurrenceType = data.recurrence?.type ?: EventRecurrenceType.NONE
                    recurrenceInterval = data.recurrence?.interval
                    recurrenceDaysOfWeek = data.recurrence?.daysOfWeek?.joinToString(",")
                    recurrenceDayOfMonth = data.recurrence?.dayOfMonth
                    recurrenceMonth = data.recurrence?.month?.let { Month.of(it) }
                }
            }
            call.respond(HttpStatusCode.Created, createdEvent.encapsulateWithUsualDto())
        }
    }
    get<RealmResource.Id.Calendar.Events> {
        authenticate { user ->
            val parameters = call.parameters
            val periodStart = parameters["from"]?.toLongOrNull()
            val periodEnd = parameters["to"]?.toLongOrNull()
            val timezoneId = parameters["timezoneId"] ?: "UTC"
            val timezone = TimeZone.of(timezoneId)
            if (periodStart == null || periodEnd == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            val events = newSuspendedTransaction {
                user.events.filter {
                    it.startDateTime.toInstant(timezone) >= Instant.fromEpochSeconds(periodStart)
                            && it.endDateTime.toInstant(timezone) <= Instant.fromEpochSeconds(periodEnd)
                }
            }
            call.respond(
                HttpStatusCode.OK, CalendarEventPluralResponseCapsule(
                    events.map {
                        it.toUsualDto()
                    }
                )
            )
        }
    }
    get<RealmResource.Id.Calendar.Events.Id> { (id) ->
        authenticate { user ->
            val event = newSuspendedTransaction { user.events.singleOrNull { it.id.value == id } }
            if (event == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(HttpStatusCode.OK, event.encapsulateWithUsualDto())
        }
    }
    delete<RealmResource.Id.Calendar.Events.Id> { (id) ->
        authenticate { user ->
            var deleted = false
            newSuspendedTransaction {
                val found = user.events.singleOrNull { it.id.value == id } ?: return@newSuspendedTransaction
                deleted = true
                found.delete()
            }
            if (!deleted) call.respond(HttpStatusCode.NotFound)
            else call.respond(HttpStatusCode.NoContent)
        }
    }
}