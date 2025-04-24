package com.erik.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ChatMembers : Table("chat_members") {
    val chatId = integer("chat_id").references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 10).default("member")
    val joinedAt = long("joined_at").nullable()

    override val primaryKey = PrimaryKey(chatId, userId)
}