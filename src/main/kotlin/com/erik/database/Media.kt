package com.erik.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Media : Table("media") {
    val id = integer("id").autoIncrement()
    val messageId = integer("message_id").references(Messages.id, onDelete = ReferenceOption.CASCADE)
    val type = varchar("type", 20) // 'image', 'video', 'document', 'audio'
    val url = varchar("url", 255)
    val caption = text("caption").nullable()
    val fileSize = integer("file_size").nullable()
    val createdAt = long("created_at").nullable()

    override val primaryKey = PrimaryKey(id)
}