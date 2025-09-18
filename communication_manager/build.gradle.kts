import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.6.0")
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation ("org.apache.camel:camel-mail:3.11.3")
    implementation("com.google.http-client:google-http-client-gson:1.39.2")
    implementation ("com.google.apis:google-api-services-gmail:v1-rev110-1.25.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.10.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.camel.springboot:camel-google-mail-starter:4.5.0")
    implementation("org.apache.camel:camel-google-mail:4.5.0")
    implementation("io.github.microutils:kotlin-logging:2.0.11")
    implementation("com.google.code.gson:gson:2.11.0")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.hibernate:hibernate-validator:7.0.1.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("io.mockk:mockk:1.12.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
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
