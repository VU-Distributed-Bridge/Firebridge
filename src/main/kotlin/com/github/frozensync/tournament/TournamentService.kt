package com.github.frozensync.tournament

import com.github.frozensync.DeviceId
import kotlinx.coroutines.Deferred

interface TournamentService {

    /**
     * Returns a live tournament by director [directorId] which device [deviceId] is assigned to.
     */
    suspend fun getLiveTournamentAsync(directorId: String, deviceId: DeviceId): Deferred<Tournament>

    fun save(score: Score)
}
