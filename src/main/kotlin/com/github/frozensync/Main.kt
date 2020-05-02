package com.github.frozensync

import com.github.frozensync.persistence.firestore.FirestoreFactory
import com.github.frozensync.raspberrypi.RaspberryPiRepositoryImpl
import com.github.frozensync.raspberrypi.RaspberryPiServiceImpl
import com.google.cloud.firestore.DocumentChange
import mu.KotlinLogging
import java.util.*
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Please provide an id")
        exitProcess(1)
    }

    val id = args[0]
    logger.info { "Started server with identifier $id" }

    val db = FirestoreFactory.get()

    db.collection("scores").addSnapshotListener { snapshot, _ ->
        val changes = snapshot?.documentChanges ?: return@addSnapshotListener
        changes.forEach { change ->
            when (change.type) {
                DocumentChange.Type.ADDED -> println("New score: ${change.document.data}")
                DocumentChange.Type.MODIFIED -> println("Modified score: ${change.document.data}")
                DocumentChange.Type.REMOVED -> println("Removed score: ${change.document.data}")
            }
        }
    }

    val piRepository = RaspberryPiRepositoryImpl(db)
    val piService = RaspberryPiServiceImpl(piRepository)
    piService.register(UUID.fromString(id))

    val scanner = Scanner(System.`in`)
    while (true) {
        val score = scanner.nextInt()
        val data: Map<String, Any> = mapOf("score" to score)

        db.collection("scores")
            .document(UUID.randomUUID().toString())
            .set(data)
    }
}
