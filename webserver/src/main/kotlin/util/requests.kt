package io.github.prismaplatform.webserver.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend inline fun <reified T : Any> ApplicationCall.receiveOrBadRequest(): T? {
    return try {
        this.receive<T>()
    } catch (exception: CannotTransformContentToTypeException) {
        this.respond(HttpStatusCode.BadRequest)
        return null
    }
}