package com.erik.utils

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import java.util.Base64

object EmailSender {
    suspend fun sendEmail(
        fromEmail: String,
        fromName: String,
        toEmail: String,
        toName: String,
        subject: String,
        textBody: String,
        htmlBody: String
    ): Boolean {
        val dotenv = Dotenv.load()
        val apiKey = dotenv["API_mailjet"]
        val apiSecret = dotenv["SECRET_mailjet"]

        println("APIs key and secret:")
        println(apiKey)
        println(apiSecret)

        val client = HttpClient(CIO)

        val response: HttpResponse = client.post("https://api.mailjet.com/v3.1/send") {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append(
                    HttpHeaders.Authorization,
                    "Basic " + Base64.getEncoder().encodeToString("$apiKey:$apiSecret".toByteArray())
                )
            }

            setBody(
                """
            {
              "Messages":[
                {
                  "From": {
                    "Email": "$fromEmail",
                    "Name": "$fromName"
                  },
                  "To": [
                    {
                      "Email": "$toEmail",
                      "Name": "$toName"
                    }
                  ],
                  "Subject": "$subject",
                  "TextPart": "$textBody",
                  "HTMLPart": "$htmlBody"
                }
              ]
            }
            """.trimIndent()
            )
        }

        println("Mailjet response: ${response.bodyAsText()}")

        if (response.status == HttpStatusCode.Companion.OK) {
            return true
        } else {
            return false
        }
    }

    suspend fun sendOTP(to: String, username: String, code: String): Boolean {
        println("sendOTP to ${to} with code of ${code}")

        return sendEmail(
            "osseo2003@gmail.com",
            "Erik",
            to,
            username,
            "Activation Code",
            "Ваш код для авторизации в MessengerApp - ${code}",
            "<h1>Ваш код для авторизации в MessengerApp - ${code}</h1>"
        )
    }
}