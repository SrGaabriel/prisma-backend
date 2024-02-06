package io.github.prismaplatform.database.dao

import io.github.prismaplatform.database.util.SnowflakeEID
import io.github.prismaplatform.database.util.SnowflakeEntity
import io.github.prismaplatform.database.util.SnowflakeEntityClass
import io.github.prismaplatform.database.util.SnowflakeIdTable

class User(id: SnowflakeEID): SnowflakeEntity(id) {
    companion object : SnowflakeEntityClass<User>(UserTable)

    var name by UserTable.name
    var email by UserTable.email
    var password by UserTable.password
    val events by CalendarEvent referrersOn CalendarEventTable.creator
}

object UserTable: SnowflakeIdTable(name="users") {
    val name = varchar("name", 32)
    val email = varchar("email", 64)
    val password = varchar("password", 60)
}