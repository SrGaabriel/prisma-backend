package io.github.prismaplatform.database

import io.github.prismaplatform.database.connection.DatabaseConnection
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseService(private vararg val connections: DatabaseConnection) {
    fun connect() {
        connections.forEach { it.connect() }
    }

    fun createTables() = transaction {

    }
}