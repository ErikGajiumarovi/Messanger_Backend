package com.erik.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Calls : Table("calls") {
    val id = integer("id").autoIncrement()
    val callerId = integer("caller_id").references(Users.id, onDelete = ReferenceOption.SET_NULL)
    val receiverId = integer("receiver_id").references(Users.id, onDelete = ReferenceOption.SET_NULL)
    val type = varchar("type", 10).nullable() // 'audio', 'video'
    val status = varchar("status", 10).nullable() // 'missed', 'answered', 'declined'
    val duration = integer("duration").nullable()
    val createdAt = long("created_at").nullable()

    override val primaryKey = PrimaryKey(id)
}