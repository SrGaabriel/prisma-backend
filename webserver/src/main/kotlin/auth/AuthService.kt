package io.github.prismaplatform.webserver.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.prismaplatform.common.Snowflake

class AuthService(val secret: String, val salt: String) {
    val hasher = BCrypt.withDefaults()
    val verifyer = BCrypt.verifyer()

    fun hashPassword(password: String): String =
        String(hasher.hash(COST, salt.toByteArray(CHARSET), password.toByteArray(CHARSET)), CHARSET)

    fun verifyPassword(password: String, hashed: String): BCrypt.Result =
        verifyer.verify(password.toByteArray(CHARSET), hashed.toByteArray(CHARSET))

    fun doesPasswordMatch(password: String, hashed: String): Boolean =
        verifyPassword(password, hashed).verified

    fun createJwtToken(id: Snowflake): String {
        return JWT.create()
            .withClaim("id", id.value)
            .sign(Algorithm.HMAC256(secret))
    }

    fun decodeToken(token: String): Snowflake? {
        val id = JWT.decode(token).getClaim("id").asLong() ?: return null
        return Snowflake(id)
    }

    companion object {
        val COST = 11
        val CHARSET = Charsets.UTF_8
    }
}