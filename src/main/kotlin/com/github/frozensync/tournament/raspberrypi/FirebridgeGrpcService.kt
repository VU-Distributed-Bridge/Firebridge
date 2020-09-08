package com.github.frozensync.tournament.raspberrypi

import com.github.frozensync.tournament.*

class FirebridgeGrpcService(
    private val tournamentService: TournamentService,
    private val directorId: String,
    private val tournamentId: String,
    private val deviceHealthStatistics: DeviceHealthStatistics
) : FirebridgeGrpcKt.FirebridgeCoroutineImplBase() {

    override suspend fun sendScore(request: ScoreRequest): Empty {
        val result = Score(
            round = request.round,
            NS = request.ns,
            EW = request.ew,
            board = request.board,
            contr = request.contr,
            lead = request.lead,
            result = request.result
        )
        tournamentService.saveScore(result, directorId, tournamentId)
        return Empty.getDefaultInstance()
    }

    override suspend fun sendBatteryPercentage(request: BatteryPercentageRequest): Empty {
        deviceHealthStatistics.batteryPercentage = request.percentage
        return Empty.getDefaultInstance()
    }

    override suspend fun sendConfiguration(request: ConfigurationRequest): Empty {
        deviceHealthStatistics.region = request.region
        deviceHealthStatistics.channel = request.channel
        return Empty.getDefaultInstance()
    }
}
