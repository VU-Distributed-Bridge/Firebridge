package com.github.frozensync.tournament

import com.google.cloud.firestore.Firestore
import mu.KotlinLogging

class TournamentServiceImpl(private val db: Firestore, private val raspberryPiId: String) : TournamentService {

    private val logger = KotlinLogging.logger { }

    override fun save(score: Score) {
        logger.entry(score)

        val scoreAsMap = mapOf("result" to score.result)

        db.runTransaction { transaction ->
            val assignmentRef = db.collection("tournamentAssignments").document(raspberryPiId)
            val assignment = transaction.get(assignmentRef).get()
            val tournamentId = assignment.getString("tournamentId") ?: return@runTransaction

            val serverRef = db.collection("tournaments").document(tournamentId)
                .collection("servers").document(raspberryPiId)
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
