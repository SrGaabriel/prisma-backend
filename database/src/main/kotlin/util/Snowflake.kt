package io.github.prismaplatform.database.util

import io.github.prismaplatform.common.Snowflake
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect

typealias SnowflakeEID = EntityID<Snowflake>

abstract class SnowflakeEntity(id: EntityID<Snowflake>): Entity<Snowflake>(id) {
    val snowflake: Snowflake get() = id.value
}

abstract class SnowflakeEntityClass<T : Entity<Snowflake>>(table: IdTable<Snowflake>)
    : EntityClass<Snowflake, T>(table)

open class SnowflakeIdTable(name: String = "", columnName: String = "id"): IdTable<Snowflake>(name) {
    final override val id: Column<EntityID<Snowflake>> = snowflake(columnName).entityId()
    final override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class SnowflakeColumnType: ColumnType() {
    override fun sqlType(): String = currentDialect.dataTypeProvider.longType()

    override fun notNullValueToDB(value: Any): Long = when (value) {
        is Snowflake -> value.value
        is Long -> value
        is Number -> value.toLong()
        else -> error("Can't convert ${value::class.qualifiedName} to Snowflake")
    }

    override fun valueFromDB(value: Any): Snowflake {
        val valueAsLong = when(value) {
            is Long -> value
            is Number -> value.toLong()
            is String -> value.toLong()
            else -> error("Unexpected value of type Snowflake: $value of ${value::class.qualifiedName}")
        }
        return Snowflake(valueAsLong)
    }
}

fun Table.snowflake(name: String) = registerColumn<Snowflake>(name, SnowflakeColumnType())