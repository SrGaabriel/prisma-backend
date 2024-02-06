package io.github.prismaplatform.webserver.route

import io.github.prismaplatform.common.Snowflake
import io.github.prismaplatform.common.SnowflakeService
import io.github.prismaplatform.common.dto.UserSignupRequest
import io.github.prismaplatform.database.dao.User
import io.github.prismaplatform.database.util.encapsulateWithUsualDto
import io.github.prismaplatform.webserver.auth.AuthService
import io.github.prismaplatform.webserver.util.receiveOrBadRequest
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.ktor.ext.inject

fun Route.userRoute() {
    val authService by inject<AuthService>()
    val snowflakeService by inject<SnowflakeService>()
    post<UsersResource> {
        val data = call.receiveOrBadRequest<UserSignupRequest>() ?: return@post
        val hashedPassword = authService.hashPassword(data.password)

        newSuspendedTransaction {
            User.new(snowflakeService.generate()) {
                this.name = data.name
                this.email = data.email
                this.password = hashedPassword
            }
        }
    }
    get<UsersResource.Id> { (userId) ->
        val user = newSuspendedTransaction { User.findById(userId) }
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(HttpStatusCode.OK, user.encapsulateWithUsualDto())
    }
}

@Resource("/users")
class UsersResource {
    @Resource("/{userId}")
    data class Id(val userId: Snowflake, val users: UsersResource)
}