package com.github.frozensync

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentChange
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
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

    val db = initializeFirestore()
    val scoresCollection = db.collection("scores")

    scoresCollection.addSnapshotListener { snapshot, _ ->
        val changes = snapshot?.documentChanges ?: return@addSnapshotListener
        changes.forEach { change ->
            when (change.type) {
                DocumentChange.Type.ADDED -> println("New score: ${change.document.data}")
                DocumentChange.Type.MODIFIED -> println("Modified score: ${change.document.data}")
                DocumentChange.Type.REMOVED -> println("Removed score: ${change.document.data}")
            }
        }
    }

    val scanner = Scanner(System.`in`)
    while (true) {
        val score = scanner.nextInt()
        val data: Map<String, Any> = mapOf("score" to score)

        scoresCollection
            .document(UUID.randomUUID().toString())
            .set(data)
    }
}

private fun initializeFirestore(): Firestore {
    Thread.currentThread().contextClassLoader.getResourceAsStream("firestore-service-account-key.json").use {
        val credentials = GoogleCredentials.fromStream(it)
        val options = FirebaseOptions.Builder()
            .setCredentials(credentials)
            .setDatabaseUrl("https://bridge-store.firebaseio.com")
            .build()
        FirebaseApp.initializeApp(options)
    }

    return FirestoreClient.getFirestore()
}
