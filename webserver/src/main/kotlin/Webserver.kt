package io.github.prismaplatform.webserver

import io.github.prismaplatform.database.DatabaseService
import io.github.prismaplatform.database.connection.PostgreDatabaseConnection
import io.github.prismaplatform.webserver.auth.AuthService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.slf4j.event.Level

fun main() {
    embeddedServer(CIO, applicationEngineEnvironment {
        connector {
            port = 3001
        }
        module {
            module()
        }
        developmentMode = false
        watchPaths = listOf("classes/kotlin/main")
    }).start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        json()
    }

    install(Resources)
    val authService = AuthService("JWT_SECRET_MOCK", "BCRYPT_SALT_MOCK")
    val databaseService = DatabaseService(
        PostgreDatabaseConnection(
            host = "127.0.0.1",
            port = "5432",
            database = "vibra",
            username = "postgres",
            password = "underarm turbofan tilt buffer throwback jolly impotence john"
        )
    )
    databaseService.connect()
    databaseService.createTables()

    install(Koin) {
        modules(org.koin.dsl.module {
            single { authService }
            single { databaseService }

        })
    }

    routing {
        route("/api/v1/") {

        }
    }
}