package com.github.frozensync.tournament

import java.util.*

data class Tournament(val id: String, val map: Map<String, Any?>) {
    val ownerId: String by map
    val name: String by map
    val date: Date by map
    val live: Boolean by map
    val assignedRaspberryPis: List<Map<String, Any?>> by map
    val deviceIds: List<String> by map
}

data class Assignment(val map: Map<String, Any?>) {
    val id: String by map
    val role: String by map
}
