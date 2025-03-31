package com.erik.database


import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import java.sql.Connection

object Database {

    private lateinit var dataSource: HikariDataSource
    // Лучше использовать логгер для класса, а не общий "Exposed"
    private val logger = LoggerFactory.getLogger(this::class.java)

    // Сделаем путь к файлу БД параметром для гибкости
    fun init(dbFilePath: String = "mydatabase.db") {
        logger.info("Initializing database connection to SQLite file: $dbFilePath")

        // Конфигурация HikariCP для SQLite
        val config = HikariConfig().apply {
            // URL для SQLite указывает на файл БД.
            // Если файла нет, он будет создан при первом подключении.
            jdbcUrl = "jdbc:sqlite:$dbFilePath"
            // Драйвер SQLite
            driverClassName = "org.sqlite.JDBC"
            // Для SQLite большой пул обычно не нужен и может вызвать проблемы (SQLITE_BUSY).
            // Часто достаточно 1 соединения. Поставим 3 для примера, но 1 может быть лучше.
            maximumPoolSize = 3
            // Оставляем ручное управление транзакциями
            isAutoCommit = false
            // SQLite по умолчанию работает близко к SERIALIZABLE. Установим явно.
            transactionIsolation = "TRANSACTION_SERIALIZABLE" // Используем строку для HikariConfig

            // Дополнительные параметры, специфичные для SQLite, если нужны:
            // addDataSourceProperty("busy_timeout", "5000") // Таймаут ожидания при блокировке файла (в миллисекундах)

            validate()
        }

        try {
            dataSource = HikariDataSource(config)
            logger.info("HikariDataSource created for $dbFilePath")

            // Подключаем Exposed к нашему dataSource
            Database.connect(dataSource)
            logger.info("Exposed connected to the database via HikariDataSource.")

            // Устанавливаем уровень изоляции по умолчанию для транзакций Exposed
            // Используем константу Connection для TransactionManager
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

            logger.info("Successfully connected to SQLite database: $dbFilePath")

        } catch (e: Exception) {
            logger.error("Failed to initialize SQLite database connection", e)
            // Пробрасываем исключение дальше, чтобы приложение знало о сбое инициализации
            throw e
        }
    }

    fun shutdown() {
        // Проверяем, что dataSource был инициализирован и еще не закрыт
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            logger.info("Shutting down database connection pool.")
            dataSource.close()
        } else {
            logger.warn("Database source was not initialized or already closed. Shutdown skipped.")
        }
    }
}