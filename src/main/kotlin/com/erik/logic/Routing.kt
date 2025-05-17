package com.erik.logic

import com.erik.routes.authRoutes
import com.erik.routes.chatRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        authRoutes()  // Подключаем роуты аутентификации
        chatRoutes()  // Подключаем роуты чатов

        // Тестовый эндпоинт
        get("/test") {
            call.respondText("Server is working!")
        }
    }
}