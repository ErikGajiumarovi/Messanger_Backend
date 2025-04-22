package com.erik.logic

import com.erik.security.generateToken
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authRoutes() {
    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        // Проверка номера, сохранение в БД
        call.respond(mapOf("status" to "OTP sent"))
    }

    post("/auth/verify") {
        val request = call.receive<VerifyRequest>()
        // Проверка OTP, генерация JWT
        val token = generateToken(userId = 123)
        call.respond(mapOf("token" to token))
    }
}


