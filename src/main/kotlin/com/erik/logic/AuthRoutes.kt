package com.erik.logic

import com.erik.database.tables.OtpCodes
import com.erik.database.tables.RefreshTokens
import com.erik.database.tables.RegistrationTempData
import com.erik.database.tables.Users
import com.erik.security.Argon2.hashPassword
import com.erik.security.Argon2.verifyPassword
import com.erik.security.OTPGenerator
import com.erik.security.JWTGenerator
import com.erik.utils.EmailSender
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


fun Route.authRoutes() {

    @Serializable
    data class LoginRequest(
        val username: String,
        val password: String
    )

    post("/auth/login") {
        val request = call.receive<LoginRequest>()

        // Проверка пароля
        val user = transaction {
            Users.select { Users.username eq request.username }.firstOrNull()
        }
        if (user == null || !verifyPassword(request.password, user[Users.password])) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        // Генерация OTP для 2FA
        val code = OTPGenerator.generateNumericOTP()
        transaction {
            OtpCodes.insert {
                it[OtpCodes.purpose] = "LOGIN"
                it[OtpCodes.user_id] = user[Users.id]
                it[OtpCodes.code] = code
                it[OtpCodes.created_at] = System.currentTimeMillis()
                it[OtpCodes.expires_at] = System.currentTimeMillis() + 5 * 60 * 1000 // 5 минут
            }
        }

        // Отправка OTP (например, через SMS или email)
        EmailSender.sendOTP(
            user[Users.email],
            user[Users.username],
            code
        )
        call.respond(mapOf("status" to "OTP sent"))
    }

    @Serializable
    data class RegisterRequest(
        val username: String,
        val email: String,
        val phoneNumber: String,
        val password: String
    )

    post("/auth/register") {
        val request = call.receive<RegisterRequest>()

        // Проверка на существующего пользователя
        val exists = transaction {
            Users.select { Users.username eq request.username }.count() > 0
        }

        if (exists) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        // Сохранение данных во временную таблицу
        val tempDataId = transaction {
            RegistrationTempData.insert {
                it[RegistrationTempData.username] = request.username
                it[RegistrationTempData.email] = request.email
                it[RegistrationTempData.phone_number] = request.phoneNumber
                it[RegistrationTempData.password_hash] = hashPassword(request.password)
                it[RegistrationTempData.created_at] = System.currentTimeMillis()
            }[RegistrationTempData.id]
        }

        // Генерация OTP
        val code = OTPGenerator.generateNumericOTP()
        transaction {
            OtpCodes.insert {
                it[OtpCodes.purpose] = "REGISTRATION"
                it[OtpCodes.temp_data_id] = tempDataId
                it[OtpCodes.code] = code
                it[OtpCodes.created_at] = System.currentTimeMillis()
                it[OtpCodes.expires_at] = System.currentTimeMillis() + 15 * 60 * 1000
            }
        }

        // Отправка OTP на email
        EmailSender.sendOTP(
            request.email,
            request.username,
            code
        )
        call.respond(mapOf("status" to "OTP sent"))
    }

    @Serializable
    data class VerifyRequest(
        val username: String,
        val otp: String
    )

    /***
     * Получаем от пользователя username и otp (одноразовый пароль)
     * Ищем совпадения в базе данных, проверяем не просрочен ли код
     * Возвращаем пользователя JWT если все в порядке
     */
    post("/auth/verify") {
        val request = call.receive<VerifyRequest>()

        // Поиск активного OTP
        val otpRecord = transaction {
            OtpCodes
                .select {
                    (OtpCodes.code eq request.otp) and
                            (OtpCodes.expires_at greater System.currentTimeMillis())
                }
                .firstOrNull()
        }

        if (otpRecord == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid OTP"))
            return@post
        }

        when (otpRecord[OtpCodes.purpose]) {
            "REGISTRATION" -> {
                // Регистрация: перенос данных из RegistrationTempData в Users
                val tempDataId = otpRecord[OtpCodes.temp_data_id] ?: run {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing temp_data_id"))
                    return@post
                }
                val tempData = transaction {
                    RegistrationTempData
                        .select { RegistrationTempData.id eq tempDataId }
                        .first()
                }

                val userId = transaction {
                    Users.insert {
                        it[Users.username] = tempData[RegistrationTempData.username]
                        it[Users.password] = tempData[RegistrationTempData.password_hash]
                        it[Users.email] = tempData[RegistrationTempData.email]
                        it[Users.phoneNumber] = tempData[RegistrationTempData.phone_number]
                    }[Users.id]
                }

                // Очистка
                transaction {
                    RegistrationTempData.deleteWhere { id eq tempData[RegistrationTempData.id] }
                    OtpCodes.deleteWhere { id eq otpRecord[OtpCodes.id] }
                }

                val tokenPair = JWTGenerator.generateTokenPair(userId)
                call.respond(tokenPair)
            }
            "LOGIN" -> {
                // 2FA: выдача токена
                val userId = otpRecord[OtpCodes.user_id]!!
                transaction {
                    OtpCodes.deleteWhere { id eq otpRecord[OtpCodes.id] }
                }

                val tokenPair = JWTGenerator.generateTokenPair(userId)
                call.respond(tokenPair)
            }
            else -> call.respond(HttpStatusCode.BadRequest)
        }
    }

    @Serializable
    data class RefreshTokenRequest(val refreshToken: String)

    post("/auth/refresh") {
        val request = call.receive<RefreshTokenRequest>()
        val refreshToken = request.refreshToken

        try {
            // Генерируем новый access token
            val newAccessToken = JWTGenerator.refreshAccessToken(refreshToken)

            call.respond(mapOf("accessToken" to newAccessToken))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
        }
    }

    @Serializable
    data class LogoutRequest(val refreshToken: String)

    post("/auth/logout") {
        val request = call.receive<LogoutRequest>()
        val refreshToken = request.refreshToken

        transaction {
            RefreshTokens.update({ RefreshTokens.token eq refreshToken }) {
                it[RefreshTokens.revoked] = true
            }
        }

        call.respond(mapOf("success" to true))
    }





}


