package com.erik.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MessageStatuses : Table("message_statuses") {
    val messageId = integer("message_id").references(Messages.id, onDelete = ReferenceOption.CASCADE)
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 10) // 'sent', 'delivered', 'read'
    val updatedAt = long("updated_at").nullable()

    override val primaryKey = PrimaryKey(messageId, userId)
}