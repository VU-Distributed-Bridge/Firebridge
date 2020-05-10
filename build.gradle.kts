plugins {
    application
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.github.frozensync.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

val firestoreVersion by extra("6.12.2")
val koinVersion by extra("2.1.5")

val kotlinLoggingVersion by extra("1.7.9")
val logbackVersion by extra("1.2.3")
val googleCloudLoggingVersion by extra("0.116.0-alpha")

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.google.firebase:firebase-admin:$firestoreVersion")

    implementation("org.koin:koin-core:$koinVersion")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.google.cloud:google-cloud-logging-logback:${googleCloudLoggingVersion}")

    testImplementation("org.koin:koin-test:$koinVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to application.mainClassName
                )
            )
        }
    }
    withType<JavaExec> {
        standardInput = System.`in`
    }
}
