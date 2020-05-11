package com.github.frozensync

import com.github.frozensync.persistence.firestore.firestoreModule
import com.github.frozensync.raspberrypi.RaspberryPiService
import com.github.frozensync.raspberrypi.raspberryPiModule
import com.github.frozensync.tournament.Score
import com.github.frozensync.tournament.TournamentService
import com.github.frozensync.tournament.tournamentModule
import mu.KotlinLogging
import org.koin.core.Koin
import org.koin.core.context.startKoin
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main() {
    val koinApplication = startKoin {
        modules(firestoreModule, raspberryPiModule, tournamentModule)
        environmentProperties()
    }
    val koin = koinApplication.koin
    koin.assertProperties()

    val id = UUID.fromString(koin.getProperty<String>(RASPBERRY_PI_ID))
    logger.info { "Started with id $id." }

    fixedRateTimer(name = "health-check", daemon = true, period = 20000L) { logger.info { "Health check: OK" } }

    val raspberryPiService = koin.get<RaspberryPiService>()
    raspberryPiService.register(id)

    val scanner = Scanner(System.`in`)
    val tournamentService = koin.get<TournamentService>()
    while (true) {
        val score = Score(scanner.nextInt())
        tournamentService.save(score)
    }
}

/**
 * Asserts required properties. Terminates application if any property is missing.
 */
private fun Koin.assertProperties() {
    if (getProperty<String>(RASPBERRY_PI_ID) == null) {
        System.err.println("Missing environment variable: RASPBERRY_PI_ID")
        exitProcess(1)
    }
    if (getProperty<String>(GOOGLE_APPLICATION_CREDENTIALS) == null) {
        System.err.println("Missing environment variable: GOOGLE_APPLICATION_CREDENTIALS")
        exitProcess(1)
    }
}
