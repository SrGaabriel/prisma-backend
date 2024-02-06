package io.github.prismaplatform.webserver.route

import io.github.prismaplatform.common.Snowflake
import io.ktor.resources.*

@Resource("/realm/")
class RealmResource {
    @Resource("/{realmId}")
    data class Id(val realmId: Snowflake, val parent: RealmResource) {
        @Resource("/calendar")
        data class Calendar(val parent: Id) {
            @Resource("/events")
            data class Events(val parent: Calendar) {
                @Resource("/{eventId}")
                data class Id(val eventId: Snowflake, val parent: Events)
            }
        }
    }
}