import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
}

group = "it.polito.wa2.g19"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Web (servlet/Tomcat) per fixare jakarta.servlet.Filter e per eventuali REST endpoint
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Security - Resource Server (JWT via Gateway/Keycloak)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Kafka consumer/producer
    implementation("org.springframework.kafka:spring-kafka")

    // MongoDB (documenti @Document)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Validazioni @Valid, @NotNull ecc. (opzionale ma utile)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jackson + Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Logging Kotlin
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Actuator (health/liveness readiness — opzionale ma consigliato)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Dev-only (compose integration)
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
