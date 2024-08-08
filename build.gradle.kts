plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    // log打印impl依赖
    implementation("ch.qos.logback:logback-classic:1.4.12")
    // 腾讯云 sdk api https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-sdk-java
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:3.1.1074")
    // 定时器 https://mvnrepository.com/artifact/org.quartz-scheduler/quartz
    implementation("org.quartz-scheduler:quartz:2.3.2")
    // .dot文件配置环境变量
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
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

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
