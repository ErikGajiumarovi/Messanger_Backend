package com.erik.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import java.sql.Connection

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/postgres" // Укажи свою БД
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10 // Количество подключений в пуле
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        dataSource = HikariDataSource(config)
        LoggerFactory.getLogger("Exposed").info("Connected to Database")
        Database.connect(dataSource)

        // Устанавливаем управление транзакциями в ручной режим (для контроля)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    }

    fun shutdown() {
        dataSource.close()
    }
}