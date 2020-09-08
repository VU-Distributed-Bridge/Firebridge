package com.github.frozensync.tournament

// other fields left out for now for easier testing
data class Score(
    val round: Long,
    val NS: Long, // north-south player pair
    val EW: Long, // east-west player pair
    val board: Long,
    val contr: String,
    val lead: String,
    val result: Long
)
