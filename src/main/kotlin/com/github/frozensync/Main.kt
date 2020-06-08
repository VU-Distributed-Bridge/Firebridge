package com.github.frozensync

import com.github.frozensync.persistence.firestore.firestoreModule
import com.github.frozensync.raspberrypi.RaspberryPiService
import com.github.frozensync.raspberrypi.raspberryPiModule
import com.github.frozensync.tournament.Score
import com.github.frozensync.tournament.TournamentService
import com.github.frozensync.tournament.tournamentModule
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.Koin
import org.koin.core.context.startKoin
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main() = runBlocking {
    val koinApplication = startKoin {
        modules(firestoreModule, raspberryPiModule, tournamentModule)
        environmentProperties()
    }
    val koin = koinApplication.koin.assertProperties()

    val id = UUID.fromString(koin.getProperty<String>(RASPBERRY_PI_ID))
    logger.info { "Started with id $id." }

    fixedRateTimer(name = "health", daemon = true, period = 20000L) { logger.info { "Health check: OK" } }

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
 * Returns a valid [Koin] instance after asserting all required properties. Terminates the program if any required property is missing.
 */
private fun Koin.assertProperties(): Koin {
    val errorMessage = StringBuilder()
    var isIllegalState = false

    if (getProperty<String>(RASPBERRY_PI_ID) == null) {
        errorMessage.appendln("Missing environment variable: RASPBERRY_PI_ID")
        isIllegalState = true
    }
    if (getProperty<String>(GOOGLE_APPLICATION_CREDENTIALS) == null) {
        errorMessage.appendln("Missing environment variable: GOOGLE_APPLICATION_CREDENTIALS")
        isIllegalState = true
    }

    if (isIllegalState) {
        System.err.println(errorMessage)
        exitProcess(1)
    } else {
        return this
    }
}
