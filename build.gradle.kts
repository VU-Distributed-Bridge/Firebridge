plugins {
    application
    kotlin("jvm") version "1.3.71"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.github.frozensync.MainKt"
}

repositories {
    mavenCentral()
}

val firestoreVersion by extra("6.12.2")

val logbackVersion by extra("1.2.3")
val kotlinLoggingVersion by extra("1.7.9")

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.google.firebase:firebase-admin:$firestoreVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<JavaExec> {
        standardInput = System.`in`
    }
}
