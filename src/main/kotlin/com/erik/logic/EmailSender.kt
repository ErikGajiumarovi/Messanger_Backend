package com.erik.logic

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.*

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
        val apiKey = System.getenv("API_mailjet")
        val apiSecret = System.getenv("SECRET_mailjet")

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

        if (response.status == HttpStatusCode.OK) {
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

