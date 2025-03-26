plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.8"
}

application {
    mainClass.set("com.erik.MainKt")
}

repositories {
    mavenCentral()
}
dependencies {
    implementation("com.zaxxer:HikariCP:5.1.0") // Connection Pool
    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
    implementation("org.postgresql:postgresql:42.7.2")

    implementation("io.ktor:ktor-server-websockets-jvm:2.3.8")
    implementation("io.ktor:ktor-server-core-jvm:2.3.8")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.8")
    implementation("io.ktor:ktor-server-content-negotiation-jvm") // JSON
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm") // Сериализация
    implementation("ch.qos.logback:logback-classic:1.4.14")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.8") // Логирование
}

kotlin {
    jvmToolchain(17)
}