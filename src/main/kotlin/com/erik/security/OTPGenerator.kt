package com.erik.security

import kotlin.random.Random

/**
 * Генератор одноразовых паролей (OTP)
 */
object OTPGenerator {

    /**
     * Генерирует числовой OTP заданной длины
     *
     * @param length Длина OTP (по умолчанию 6 цифр)
     * @return Строка с OTP
     */
    fun generateNumericOTP(length: Int = 6): String {
        val allowedChars = ('0'..'9')
        return generateOTP(length, allowedChars.toList())
    }

    /**
     * Генерирует буквенно-цифровой OTP заданной длины
     *
     * @param length Длина OTP (по умолчанию 8 символов)
     * @param includeSpecialChars Включать ли специальные символы (по умолчанию false)
     * @return Строка с OTP
     */
    fun generateAlphanumericOTP(length: Int = 8, includeSpecialChars: Boolean = false): String {
        val letters = ('A'..'Z') + ('a'..'z')
        val digits = ('0'..'9')
        val specialChars = listOf('!', '@', '#', '$', '%', '&', '*')

        val allowedChars = if (includeSpecialChars) {
            letters + digits + specialChars
        } else {
            letters + digits
        }

        return generateOTP(length, allowedChars)
    }

    /**
     * Базовая функция для генерации OTP из заданного набора символов
     *
     * @param length Длина OTP
     * @param allowedChars Список разрешенных символов
     * @return Строка с OTP
     */
    private fun generateOTP(length: Int, allowedChars: List<Char>): String {
        return (1..length)
            .map { Random.nextInt(0, allowedChars.size) }
            .map(allowedChars::get)
            .joinToString("")
    }

    /**
     * Генерирует OTP по шаблону (например, "XXX-XXX", где X заменяется на цифру)
     *
     * @param pattern Шаблон OTP, где 'X' будет заменена на случайную цифру
     * @return Строка с OTP согласно шаблону
     */
    fun generatePatternOTP(pattern: String): String {
        val result = StringBuilder(pattern)

        for (i in result.indices) {
            if (result[i] == 'X') {
                result[i] = Random.nextInt(0, 10).toString()[0]
            }
        }

        return result.toString()
    }
}