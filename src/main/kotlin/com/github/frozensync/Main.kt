package com.github.frozensync

import com.github.frozensync.database.firestoreModule
import com.github.frozensync.tournament.ScorerServer
import com.github.frozensync.tournament.TournamentService
import com.github.frozensync.tournament.raspberrypi.RaspberryPiService
import com.github.frozensync.tournament.tournamentModule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main(): Unit = runBlocking {
    val koin = startKoin {
        modules(mainModule, firestoreModule, tournamentModule)
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

    val tournamentService = koin.get<TournamentService>()
    tournamentService.listenForLiveTournaments()
        .filter { it.isNotEmpty() }
        .take(1)
        .map { it[0] }
        .collect {
            logger.info { "Found a live tournament! Will start working for \"${it.name}\"." }
        }

    val server = koin.get<ScorerServer>()
    server.start().blockUntilShutdown()

    logger.info { "Shutdown FireBridge" }
}
