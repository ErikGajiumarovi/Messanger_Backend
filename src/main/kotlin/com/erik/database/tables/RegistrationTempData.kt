package com.erik.database.tables

import org.jetbrains.exposed.sql.Table

object RegistrationTempData : Table("registration_temp_data") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val phone_number = varchar("phone_number", 20).uniqueIndex()
    val password_hash = text("password_hash") // Хеш пароля
    val created_at = long("created_at")

    override val primaryKey = PrimaryKey(id)
}