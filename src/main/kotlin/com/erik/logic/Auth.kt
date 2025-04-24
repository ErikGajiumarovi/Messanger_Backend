package com.erik.logic

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.erik.security.Jwt
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt {
            realm = Jwt.ISSUER
            verifier(
                JWT.require(Algorithm.HMAC256(Jwt.SECRET))
                    .withIssuer(Jwt.ISSUER)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

