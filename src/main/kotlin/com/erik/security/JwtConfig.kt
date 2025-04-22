package com.erik.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

private val SECRET = System.getenv("JWT_SECRET")
private const val ISSUER = "Erik's_Messenger_Application"

private val algorithm = Algorithm.HMAC256(SECRET)

fun generateToken(userId: Int): String {
    return JWT.create()
        .withIssuer(ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000)) // 1 час
        .sign(algorithm)
}

data class RegisterRequest(val phoneNumber: String)
data class VerifyRequest(val phoneNumber: String, val otp: String)