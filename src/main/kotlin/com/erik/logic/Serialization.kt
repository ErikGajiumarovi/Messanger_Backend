package com.erik.logic

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true    // Красивое форматирование JSON (для удобства отладки)
            ignoreUnknownKeys = true  // Игнорирует лишние поля в JSON
            isLenient = true     // Разрешает нестрогий JSON (например, кавычки)
        })
    }
}