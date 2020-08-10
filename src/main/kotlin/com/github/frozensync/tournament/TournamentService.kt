package com.github.frozensync.tournament

import com.github.frozensync.DeviceId
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface TournamentService {

    /**
     * Returns a live tournament by director [directorId] to which device [deviceId] is assigned to.
     */
    suspend fun getLiveTournamentAsync(directorId: String, deviceId: DeviceId): Deferred<Tournament>

    /**
     * Returns a flow of incoming scores.
     */
    suspend fun streamScores(directorId: String, tournamentId: String): Flow<Score>

    /**
     * Adds a [score] for tournament [tournamentId] by director [directorId].
     */
    suspend fun saveScore(score: Score, directorId: String, tournamentId: String)
}
