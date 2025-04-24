package com.erik.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object RefreshTokens : Table("refresh_tokens") {
    val id = integer("id").autoIncrement()

    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val token = varchar("token", 255)
    val expires_at = long("expires_at")
    val created_at = long("created_at")
    val revoked = bool("revoked").default(false)
}