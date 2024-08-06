plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("ch.qos.logback:logback-classic:1.4.12")
    // https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-sdk-java
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:3.1.1074")
    implementation("com.google.code.gson:gson:2.9.0")  // json
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}