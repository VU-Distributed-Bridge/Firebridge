package com.github.frozensync.tournament

import com.github.frozensync.DeviceId
import com.github.frozensync.database.retry
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import mu.KotlinLogging

class TournamentServiceImpl(private val db: Firestore) : TournamentService {

    private val logger = KotlinLogging.logger { }

    override suspend fun getLiveTournamentAsync(directorId: String, deviceId: DeviceId): Deferred<Tournament> {
        logger.entry(directorId, deviceId)

        val result = CompletableDeferred<Tournament>()

        val listener = db.collection("directors/$directorId/tournaments")
            .whereEqualTo("live", true)
            .whereArrayContains("deviceIds", deviceId.toString())
            .addSnapshotListener listener@{ snapshots, error ->
                if (error != null) {
                    result.completeExceptionally(error)
                    return@listener logger.catching(error)
                }

                val docs = snapshots!!.documents
                if (docs.isEmpty()) return@listener logger.debug { "No live tournament found" }
                if (docs.size > 2) return@listener logger.error { "Inconsistent data: found more than 2 live tournaments" }

                val doc = docs[0]
                result.complete(Tournament(doc.id, doc.data))
            }
        result.invokeOnCompletion { listener.remove() }

        logger.exit()
        return result
    }

    override suspend fun saveScore(score: Score, directorId: String, tournamentId: String) {
        logger.entry(score, directorId, tournamentId)

        db.retry {
            @Suppress("BlockingMethodInNonBlockingContext")
            collection("directors/$directorId/tournaments/$tournamentId/scores")
                .add(score)
                .get()
        }

        logger.exit()
    }
}
