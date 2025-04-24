package com.erik

import com.erik.database.DatabaseFactory
import com.erik.logic.EmailSender
import com.erik.logic.configureAuth
import com.erik.logic.configureRouting
import com.erik.logic.configureSerialization
import com.erik.logic.configureWebSockets
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    DatabaseFactory.init()
    embeddedServer(Netty, port = 8080) {
        configureSerialization()
        configureAuth()
        configureRouting()
        configureWebSockets()
    }.start(wait = true)
}




