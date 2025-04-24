plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.8"
    kotlin("plugin.serialization") version "1.9.0"
}

application {
    mainClass.set("com.erik.MainKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
dependencies {
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
    implementation("org.postgresql:postgresql:42.7.2")

    implementation("io.ktor:ktor-server-websockets-jvm:2.3.8")
    implementation("io.ktor:ktor-server-core-jvm:2.3.8")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.8")
    implementation("io.ktor:ktor-server-content-negotiation-jvm") // JSON
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm") // Сериализация
    implementation("io.ktor:ktor-client-cio:2.3.8")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // JWT (для аутентификации)
    implementation("io.ktor:ktor-server-auth:2.3.8")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.8")

    // Email
    implementation("org.apache.commons:commons-email:1.5")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Hashing
    implementation("org.bouncycastle:bcprov-jdk18on:1.78") // Основная библиотека для Argon2
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") // Стандартная библиотека Kotlin


}

kotlin {
    jvmToolchain(17)
}