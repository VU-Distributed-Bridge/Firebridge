package com.github.frozensync.tournament

import com.github.frozensync.Configuration
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mu.KotlinLogging

class TournamentServiceImpl(private val configuration: Configuration, private val db: Firestore) : TournamentService {

    private val logger = KotlinLogging.logger { }

    override suspend fun listenForLiveTournaments(): Flow<List<TournamentData>> =
        callbackFlow {
            val listener = db.collection("tournaments")
                .whereEqualTo("live", true)
                .whereArrayContains("deviceIds", configuration.deviceId.toString())
                .addSnapshotListener { snapshots, error ->
                    if (error != null) return@addSnapshotListener logger.catching(error)

                    val result = snapshots?.toObjects(TournamentData::class.java) ?: emptyList()
                    sendBlocking(result)
                }

            logger.info { "Waiting for a tournament to go live..." }
            awaitClose { listener.remove() }
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
