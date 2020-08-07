import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
    idea
    application
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.72"
    id("com.google.protobuf") version "0.8.12"
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

val kotlinCoroutinesVersion by extra("1.3.8")
val firestoreVersion by extra("6.15.0")
val koinVersion by extra("2.1.6")
val grpcVersion = "1.31.0"
val grpcKotlinVersion = "0.1.4"
val protobufVersion = "3.12.4"
val kotlinLoggingVersion by extra("1.8.3")
val logbackVersion by extra("1.2.3")
val googleCloudLoggingVersion by extra("0.118.1-alpha")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

    implementation("com.google.firebase:firebase-admin:$firestoreVersion")

    implementation("org.koin:koin-core:$koinVersion")
    testImplementation("org.koin:koin-test:$koinVersion")

    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:${grpcVersion}")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.google.cloud:google-cloud-logging-logback:${googleCloudLoggingVersion}")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}


configure<NoArgExtension> {
    annotation("com.github.frozensync.database.FirestoreDocument")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
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
    withType<ShadowJar> {
        mergeServiceFiles {
            setPath("META-INF/services")
            include("io.grpc.*")
        }
    }
}
