package com.erik.database.tables

import org.jetbrains.exposed.sql.Table

object Chats : Table("chats") {
    val id = integer("id").autoIncrement()
    val type = varchar("type", 10) // должно быть 'private' или 'group'
    val name = varchar("name", 100).nullable()
    val description = text("description").nullable()
    val creatorId = integer("creator_id").references(Users.id).nullable()
    val createdAt = long("created_at").nullable()
    val avatarUrl = varchar("avatar_url", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}