package com.erik.security

import com.auth0.jwt.JWT
import com.erik.database.tables.RefreshTokens
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object JWTGenerator {
    private fun generateToken(userId: Int): String {
        return JWT.create()
            .withIssuer(Jwt.ISSUER)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000)) // 1 час
            .sign(Jwt.ALGORITHM)
    }

    @Serializable
    data class TokenPair(val accessToken: String, val refreshToken: String)

    fun generateTokenPair(userId: Int): TokenPair {
        val day30 = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000

        val accessToken = generateToken(userId)

        val refreshToken = JWT.create()
            .withIssuer(Jwt.ISSUER)
            .withClaim("userId", userId)
            .withExpiresAt(Date(day30))
            .sign(Jwt.ALGORITHM)

        // Сохраняем refresh token в базе данных
        transaction {
            RefreshTokens.insert {
                it[RefreshTokens.userId] = userId
                it[RefreshTokens.token] = refreshToken
                it[RefreshTokens.expires_at] = day30
                it[RefreshTokens.created_at] = System.currentTimeMillis()
            }
        }

        return TokenPair(accessToken, refreshToken)
    }

    fun refreshAccessToken(refreshToken: String): String {
        val verifier = JWT.require(Jwt.ALGORITHM).withIssuer(Jwt.ISSUER).build()
        val decodedJWT = verifier.verify(refreshToken)
        val userId = decodedJWT.getClaim("userId").asInt()

        // Проверьте, не был ли refresh token отозван
        if (isTokenRevoked(refreshToken)) {
            throw Exception("Token is Revoked")
        }

        // Генерируем новый access token
        return generateToken(userId)
    }

    fun isTokenRevoked(refreshToken: String): Boolean {
        return transaction {
            try {
                val token = RefreshTokens.select {
                    RefreshTokens.token eq refreshToken and
                            (RefreshTokens.revoked eq false) and
                            (RefreshTokens.expires_at greater System.currentTimeMillis())
                }.firstOrNull()

                // Если токен не найден или срок его действия истек, считаем его отозванным
                token == null
            } catch (e: Exception) {
                e.printStackTrace()
                true
            }
        }
    }
}

