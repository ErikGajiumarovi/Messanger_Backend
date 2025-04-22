package com.erik.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Blocks : Table("blocks") {
    val blockerId = integer("blocker_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val blockedId = integer("blocked_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = long("created_at").nullable()

    override val primaryKey = PrimaryKey(blockerId, blockedId)
}