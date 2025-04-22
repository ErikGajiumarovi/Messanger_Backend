package com.erik.database

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val phoneNumber = varchar("phone_number", 20).uniqueIndex()
    val name = varchar("name", 100).nullable()
    val avatarUrl = varchar("avatar_url", 255).nullable()
    val about = text("about").nullable()
    val lastSeen = long("last_seen").nullable()
    val online = bool("online").default(false)
    val createdAt = long("created_at").nullable()
    val updatedAt = long("updated_at").nullable()
    val deviceId = varchar("device_id", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}