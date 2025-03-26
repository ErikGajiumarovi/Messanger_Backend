package com.erik.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant

object Messages : Table() {
    val id = integer("id").autoIncrement()
    val chatId = varchar("chat_id", 255)
    val sender = varchar("sender", 255)
    val receiver = varchar("receiver", 255)
    val message = text("message")
    val timestamp = long("timestamp")  // Просто BIGINT без DEFAULT
    override val primaryKey = PrimaryKey(id)
}

suspend fun saveMessage(chatId: String, sender: String, receiver: String, message: String) {
    withContext(Dispatchers.IO) {
        newSuspendedTransaction {
            Messages.insert {
                it[Messages.chatId] = chatId
                it[Messages.sender] = sender
                it[Messages.receiver] = receiver
                it[Messages.message] = message
                it[Messages.timestamp] = Instant.now().toEpochMilli()  // Текущее время в миллисекундах
            }
        }
        println("✅ Сообщение сохранено: [$sender -> $receiver]: $message")
    }
}

suspend fun getChatHistory(chatId: String): List<Map<String, Any>> {
    return withContext(Dispatchers.IO) {
        newSuspendedTransaction {
            Messages.select { Messages.chatId eq chatId }
                .orderBy(Messages.timestamp, SortOrder.ASC)
                .map {
                    mapOf(
                        "id" to it[Messages.id],
                        "sender" to it[Messages.sender],
                        "receiver" to it[Messages.receiver],
                        "message" to it[Messages.message],
                        "timestamp" to it[Messages.timestamp]
                    )
                }
        }
    }
}

// com.erik.database
suspend fun getUserChats(username: String): List<String> {
    return newSuspendedTransaction {
        // Получаем все уникальные chat_id, где пользователь был отправителем или получателем
        (Messages.slice(Messages.chatId)
            .select { Messages.sender.eq(username) or Messages.receiver.eq(username) }
            .withDistinct()
            .map { it[Messages.chatId] })
            .distinct() // Дополнительная дедупликация на случай
    }
}