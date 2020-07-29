package com.github.frozensync.tournament

import com.github.frozensync.persistence.firestore.FirestoreDocument
import java.util.*

@FirestoreDocument
data class TournamentData(
    val ownerId: String,
    val name: String,
    val date: Date,
    val live: Boolean,
    val assignedRaspberryPis: List<Assignment>,
    val deviceIds: List<String>
)

@FirestoreDocument
data class Assignment(val id: String, val role: String)
