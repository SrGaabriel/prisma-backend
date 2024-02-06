package io.github.prismaplatform.database.connection

import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

interface DatabaseConnection {
    fun connect()
}

class PostgreDatabaseConnection(
    private val host: String,
    private val port: String,
    private val database: String,
    private val username: String,
    private val password: String
) : DatabaseConnection {
    override fun connect() {
        val hikariDataSource = HikariDataSource().also { datasource ->
            datasource.jdbcUrl = "jdbc:postgresql://${host}:${port}/${database}?useTimezone=true&serverTimezone=UTC"
            datasource.username = username
            datasource.password = password
        }
        Database.connect(hikariDataSource)
    }
}