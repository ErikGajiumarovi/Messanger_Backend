package com.erik

import com.erik.database.DatabaseFactory
import com.erik.database.getUserChats
import com.erik.database.saveMessage
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

fun main() {
    DatabaseFactory.init()
    embeddedServer(Netty, port = 80) {
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
        }

        routing {
            get("/test") {
                call.respondText("Server is working")
            }

            // Новый эндпоинт для получения списка чатов пользователя
            get("/chats/{username}") {
                val username = call.parameters["username"]
                if (username == null) {
                    call.respondText("Username is required")
                    return@get
                }

                val chats = getUserChats(username)
                call.respond(chats)
            }

            val clients = ConcurrentHashMap<String, DefaultWebSocketSession>()

            webSocket("/chat/{username}") {
                val username = call.parameters["username"]
                if (username == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No username"))
                    return@webSocket
                }

                clients[username] = this
                println("$username подключился!")

                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            println("[$username]: $text")

                            // Сохранение в базу данных
                            saveMessage(chatId = "global", sender = username, receiver = "all", message = text)

                            // Рассылка сообщения всем клиентам
                            clients.values.forEach { session ->
                                session.send("[$username]: $text")
                            }
                        }
                    }
                } finally {
                    clients.remove(username)
                    println("$username отключился!")
                }
            }
        }
    }.start(wait = true)
}