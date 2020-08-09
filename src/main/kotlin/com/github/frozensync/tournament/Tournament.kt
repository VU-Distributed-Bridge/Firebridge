package com.github.frozensync.tournament

import com.github.frozensync.database.FirestoreDocument
import java.util.*

data class Tournament(val id: String, val map: Map<String, Any?>) {
    val ownerId: String by map
    val name: String by map
    val date: Date by map
    val live: Boolean by map
    val assignedRaspberryPis: List<Assignment> by map
    val deviceIds: List<String> by map
}

@FirestoreDocument
data class Assignment(val id: String, val role: String)
