package com.app.fieldsync.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService {

    fun generateToken(number: String): String {
        return JWT.create().withIssuer("fieldSync").withClaim("number", number)
            .withExpiresAt(Date(System.currentTimeMillis() + 36_000_00 * 24))
            .sign(Algorithm.HMAC256("secret"))
    }

}