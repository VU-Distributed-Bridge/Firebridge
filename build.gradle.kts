import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
    idea
    application
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.71"
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

val kotlinCoroutinesVersion by extra("1.3.5")
val firestoreVersion by extra("6.12.2")
val koinVersion by extra("2.1.5")
val grpcVersion = "1.30.0"
val grpcKotlinVersion = "0.1.3"
val protobufVersion = "3.12.2"
val kotlinLoggingVersion by extra("1.7.9")
val logbackVersion by extra("1.2.3")
val googleCloudLoggingVersion by extra("0.116.0-alpha")

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
    annotation("com.github.frozensync.persistence.firestore.FirestoreDocument")
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
    withType<ShadowJar> {
        mergeServiceFiles {
            setPath("META-INF/services")
            include("io.grpc.*")
        }
    }
}
