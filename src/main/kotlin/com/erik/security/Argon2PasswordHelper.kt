package com.erik.security


import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.security.SecureRandom
import java.util.Base64

object Argon2 {
    private const val TYPE = Argon2Parameters.ARGON2_id
    private const val MEMORY = 65536 // 64 MB
    private const val ITERATIONS = 10
    private const val PARALLELISM = 2
    private const val HASH_LENGTH = 32
    private const val SALT_LENGTH = 16
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()

    // Формат: v=version$m=memory,t=iterations,p=parallelism$salt$hash
    private const val DELIMITER = "$"

    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = calculateHash(password, salt)

        return listOf(
            "v=19", // Версия Argon2
            "m=${MEMORY},t=${ITERATIONS},p=${PARALLELISM}",
            encoder.encodeToString(salt),
            encoder.encodeToString(hash)
        ).joinToString(DELIMITER)
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        val parts = storedHash.split(DELIMITER)
        if (parts.size != 4) throw IllegalArgumentException("Invalid hash format")

        val (version, params, saltStr, hashStr) = parts
        val salt = decoder.decode(saltStr)
        val storedHash = decoder.decode(hashStr)

        // Извлечение параметров
        val paramsMap = params.split(",").associate {
            val (key, value) = it.split("=")
            key to value.toInt()
        }

        val calculatedHash = calculateHash(
            password,
            salt,
            paramsMap["m"] ?: MEMORY,
            paramsMap["t"] ?: ITERATIONS,
            paramsMap["p"] ?: PARALLELISM
        )

        return constantTimeEquals(storedHash, calculatedHash)
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom.getInstanceStrong()
        return ByteArray(SALT_LENGTH).apply { random.nextBytes(this) }
    }

    private fun calculateHash(
        password: String,
        salt: ByteArray,
        memory: Int = MEMORY,
        iterations: Int = ITERATIONS,
        parallelism: Int = PARALLELISM
    ): ByteArray {
        val parameters = Argon2Parameters.Builder(TYPE)
            .withSalt(salt)
            .withMemoryAsKB(memory)
            .withIterations(iterations)
            .withParallelism(parallelism)
            .build()

        val generator = Argon2BytesGenerator()
        generator.init(parameters)

        val result = ByteArray(HASH_LENGTH)
        generator.generateBytes(password.toCharArray().map { it.code.toByte() }.toByteArray(), result)

        return result
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        return a.foldIndexed(0) { index, acc, byte ->
            acc or (byte.toInt() xor b[index].toInt())
        } == 0
    }
}