package com.github.frozensync.tournament

import kotlinx.coroutines.flow.Flow

interface TournamentService {

    /**
     * Returns a flow of real-time updates of live tournaments.
     */
    suspend fun listenForLiveTournaments(): Flow<List<TournamentData>>

    fun save(score: Score)
}
