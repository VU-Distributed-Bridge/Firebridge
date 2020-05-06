package com.github.frozensync

import com.github.frozensync.persistence.firestore.firestoreModule
import com.github.frozensync.raspberrypi.RaspberryPiService
import com.github.frozensync.raspberrypi.raspberryPiModule
import mu.KotlinLogging
import org.koin.core.context.startKoin
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Please provide an id for the raspberry pi.")
        exitProcess(1)
    }
    val id = UUID.fromString(args[0])

    logger.info { "Started with id $id." }

    val koinApplication = startKoin {
        modules(firestoreModule, raspberryPiModule)
    }
    val koin = koinApplication.koin

    val raspberryPiService = koin.get<RaspberryPiService>()
    raspberryPiService.register(id)

    val scanner = Scanner(System.`in`)
    while (true) {
        scanner.nextInt()
    }
}
