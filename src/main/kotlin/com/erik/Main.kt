package com.erik

import com.erik.database.DatabaseFactory
import com.erik.logic.configureJWT
import com.erik.logic.configureRouting
import com.erik.logic.configureSerialization
import com.erik.logic.configureWebSockets
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    DatabaseFactory.init() // Инициализация БД
    embeddedServer(Netty, port = 8080) { // Запуск сервера на Netty (порт 8080)
        configureSerialization() // Настройка сериализации (JSON)
        configureJWT()          // Настройка JWT-аутентификации
        configureRouting()      // Настройка маршрутов (API)
        configureWebSockets()   // Настройка WebSocket (если есть)
    }.start(wait = true) // Запуск сервера (wait = true — блокирует поток)
}




