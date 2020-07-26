package com.github.frozensync

import com.github.frozensync.persistence.firestore.firestoreModule
import com.github.frozensync.raspberrypi.RaspberryPiService
import com.github.frozensync.raspberrypi.raspberryPiModule
import com.github.frozensync.tournament.tournamentModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main(): Unit = runBlocking {
    val koin = startKoin {
        modules(mainModule, firestoreModule, raspberryPiModule, tournamentModule)
        environmentProperties()
    }.koin
    val errorMessage = koin.validateConfiguration()
    if (errorMessage.isNotEmpty()) {
        System.err.println(errorMessage)
        exitProcess(1)
    }

    val raspberryPiService = koin.get<RaspberryPiService>()
    raspberryPiService.register()
    raspberryPiService.scheduleHealthCheck()

    logger.info { "Started FireBridge" }
    delay(100000L)
}
