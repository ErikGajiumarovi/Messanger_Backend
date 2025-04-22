package com.erik.security

import com.auth0.jwt.algorithms.Algorithm

object Jwt {
    val SECRET = System.getenv("JWT_SECRET") ?: "fallback-secret"
    const val ISSUER = "Erik's_Messenger_Application"
    val ALGORITHM = Algorithm.HMAC256(SECRET)
}