package com.erik.database.tables

import org.jetbrains.exposed.sql.Table

object OtpCodes : Table("otp_codes") {
    val id = integer("id").autoIncrement()
    val code = varchar("code", 6) // OTP-код
    val purpose = varchar("purpose", 20) // "REGISTRATION" или "LOGIN" (2FA)
    val user_id = integer("user_id").nullable() // Ссылка на существующего пользователя (для 2FA)
    val temp_data_id = integer("temp_data_id").nullable() // Ссылка на временные данные (для регистрации)
    val created_at = long("created_at") // Время создания
    val expires_at = long("expires_at") // Время истечения


    override val primaryKey = PrimaryKey(id)
}