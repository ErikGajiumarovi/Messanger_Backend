package com.erik.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Contacts : Table("contacts") {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val contactId = integer("contact_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 10).default("pending") // 'pending', 'accepted', 'rejected'
    val createdAt = long("created_at").nullable()

    override val primaryKey = PrimaryKey(userId, contactId)
}