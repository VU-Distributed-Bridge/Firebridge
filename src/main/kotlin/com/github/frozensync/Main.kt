package com.github.frozensync

import com.github.frozensync.database.firestoreModule
import com.github.frozensync.tournament.*
import com.github.frozensync.tournament.raspberrypi.RaspberryPiService
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.collect
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

    logger.info { "Started FireBridge" }

    val configuration = koin.get<Configuration>()
    val deviceId = configuration.deviceId

    val raspberryPiService = koin.get<RaspberryPiService>()
    raspberryPiService.register(deviceId)
    raspberryPiService.scheduleHealthCheck()

    logger.info { "Waiting for device to be claimed by an owner..." }
    val directorId = raspberryPiService.getOwnerIdAsync(deviceId).await()
    logger.info { "Device is claimed by owner $directorId" }

    val tournamentService = koin.get<TournamentService>()
    logger.info { "Waiting for a tournament to go live..." }
    val tournament = tournamentService.getLiveTournamentAsync(directorId, deviceId).await()
    logger.info { "Found a live tournament! Will start working for \"${tournament.name}\"" }

    val assignment = tournament.assignedRaspberryPis.map { Assignment(it) }.first { it.id == deviceId.toString() }
    if (assignment.role == "slave") {
        val scorerService = ScorerService(tournamentService, directorId, tournament.id)
        val grpcServer = ScorerServer(configuration.grpcServerPort, scorerService)
        grpcServer.start().blockUntilShutdown()
    } else { // master
        val controlProgramClient = ControlProgramClient(
            ManagedChannelBuilder.forAddress("localhost", 8981)
                .usePlaintext()
                .executor(Dispatchers.Default.asExecutor())
                .build()
        )
        tournamentService.streamScores(directorId, tournament.id)
            .collect {
                logger.debug { it }
                controlProgramClient.sendScore(it)
            }
    }
}
