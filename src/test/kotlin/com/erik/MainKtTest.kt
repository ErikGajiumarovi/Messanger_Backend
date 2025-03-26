package com.erik

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import org.junit.Test

class MainKtTest {

    @Test
    fun testGetTest() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        client.get("/test").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testWebsocketChat() = testApplication {
        application {
            TODO("Add the Ktor module for the test")
        }
        val client = createClient {
            install(WebSockets)
        }
        client.webSocket("/chat") {
            TODO("Please write your test here")
        }
    }
}