package io.github.prismaplatform.webserver.auth

import io.github.prismaplatform.database.dao.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.ktor.ext.inject

suspend inline fun PipelineContext<Unit, ApplicationCall>.authenticate(scope: (User) -> Unit) =
    authenticateCatching { call.respond(HttpStatusCode.Unauthorized); null }?.let(scope)

suspend inline fun PipelineContext<Unit, ApplicationCall>.authenticateOrNull(): User? =
    authenticateCatching { null }

suspend inline fun PipelineContext<Unit, ApplicationCall>.authenticateCatching(fallback: () -> User?): User? {
    val authService by call.inject<AuthService>()

    val headerToken = call.request.header(HttpHeaders.Authorization)?.substring(7) ?: return fallback()
    val userId = authService.decodeToken(headerToken) ?: return fallback()

    return newSuspendedTransaction { User.findById(userId) }
}