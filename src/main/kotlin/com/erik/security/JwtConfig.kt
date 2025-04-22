package com.erik.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

fun generateToken(userId: Int): String {
    return JWT.create()
        .withIssuer(Jwt.ISSUER)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000)) // 1 час
        .sign(Jwt.ALGORITHM)
}