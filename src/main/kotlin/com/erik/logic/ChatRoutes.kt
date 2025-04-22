package com.erik.logic

import io.ktor.server.application.call
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.chatRoutes() {
    authenticate {
        // Создать чат
        post("/chats") {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
            val request = call.receive<CreateChatRequest>()

            // Сохранить в БД (используйте DatabaseFactory)
            call.respond(mapOf("chatId" to 1))
        }

        // Получить список чатов
        get("/chats") {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
            // Запрос к БД: SELECT * FROM chats WHERE id IN (SELECT chat_id FROM chat_members WHERE user_id = ?)
            call.respond(listOf<Chat>())
        }
    }
}

data class CreateChatRequest(val userIds: List<Int>, val type: String)
data class Chat(val id: Int, val name: String?)