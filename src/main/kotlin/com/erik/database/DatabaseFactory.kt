package com.erik.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.Connection

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        dataSource = HikariDataSource(config)
        LoggerFactory.getLogger("Exposed").info("Connected to Database")
        Database.connect(dataSource)

        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ

        // Создание таблиц
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Users,
                Chats,
                ChatMembers,
                Messages,
                Media,
                MessageStatuses,
                Contacts,
                Calls,
                Blocks
            )
        }
    }

    fun shutdown() {
        dataSource.close()
    }
}