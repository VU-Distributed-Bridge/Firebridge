package com.github.frozensync.tournament

import com.github.frozensync.DeviceId
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

    override fun save(score: Score) {
        logger.entry(score)

        val deviceId = configuration.deviceId.toString()
        val scoreAsMap = mapOf("result" to score.result)

        db.runTransaction { transaction ->
            val assignmentRef = db.collection("tournamentAssignments").document(deviceId)
            val assignment = transaction.get(assignmentRef).get()
            val tournamentId = assignment.getString("tournamentId") ?: return@runTransaction

            val serverRef = db.collection("tournaments").document(tournamentId)
                .collection("servers").document(deviceId)
            val server = transaction.get(serverRef).get()

            if (server.exists()) {
                @Suppress("UNCHECKED_CAST") val scores = server.get("scores") as MutableList<Map<String, Int>>
                scores.add(scoreAsMap)

                transaction.update(serverRef, "scores", scores)
            } else {
                val data = mapOf("scores" to listOf(scoreAsMap))
                transaction.set(serverRef, data)
            }
        }

        logger.exit()
    }
}
