package com.erik.database

import com.erik.database.tables.Blocks
import com.erik.database.tables.Calls
import com.erik.database.tables.ChatMembers
import com.erik.database.tables.Chats
import com.erik.database.tables.Contacts
import com.erik.database.tables.Media
import com.erik.database.tables.MessageStatuses
import com.erik.database.tables.Messages
import com.erik.database.tables.OtpCodes
import com.erik.database.tables.RefreshTokens
import com.erik.database.tables.RegistrationTempData
import com.erik.database.tables.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.Connection

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init() {
        // Загрузка переменных окружения из файла .env
        val dotenv = Dotenv.load()

        // Получение учетных данных из переменных окружения
        val dbHost = dotenv["DB_HOST"]
        val dbName = dotenv["DB_NAME"]
        val dbUser = dotenv["DB_USER"]
        val dbPassword = dotenv["DB_PASSWORD"]

        val jdbcUrl = "jdbc:postgresql://$dbHost/$dbName?sslmode=require&user=$dbUser&password=$dbPassword"

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
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
                Blocks,
                RegistrationTempData,
                OtpCodes,
                RefreshTokens
            )
        }
    }

    fun shutdown() {
        dataSource.close()
    }
}