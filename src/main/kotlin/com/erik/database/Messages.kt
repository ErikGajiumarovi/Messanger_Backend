package com.erik.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Messages : Table("messages") {
    val id = integer("id").autoIncrement()
    val chatId = integer("chat_id").references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val senderId = integer("sender_id").references(Users.id, onDelete = ReferenceOption.SET_NULL)
    val text = text("text").nullable()
    val isDeleted = bool("is_deleted").default(false)
    val createdAt = long("created_at").nullable()
    val updatedAt = long("updated_at").nullable()

    override val primaryKey = PrimaryKey(id)
}