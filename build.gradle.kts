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

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.google.firebase:firebase-admin:$firestoreVersion")

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
