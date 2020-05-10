package com.github.frozensync

import com.github.frozensync.persistence.firestore.firestoreModule
import com.github.frozensync.raspberrypi.RaspberryPiService
import com.github.frozensync.raspberrypi.raspberryPiModule
import mu.KotlinLogging
import org.koin.core.context.startKoin
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main() {
    val koinApplication = startKoin {
        modules(firestoreModule, raspberryPiModule)
        environmentProperties()
    }
    val koin = koinApplication.koin

    val idProperty = koin.getProperty<String>("RASPBERRY_PI_ID")
    if (idProperty == null) {
        System.err.println("Please provide an id for the raspberry pi as an environment variable.")
        exitProcess(1)
    }
    val id = UUID.fromString(idProperty)

    logger.info { "Started with id $id." }

    val raspberryPiService = koin.get<RaspberryPiService>()
    raspberryPiService.register(id)

    val scanner = Scanner(System.`in`)
    while (true) {
        scanner.nextInt()
    }
}
