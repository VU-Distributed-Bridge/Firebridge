package com.github.frozensync

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentChange
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.util.*

fun main() {
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
    val serviceAccount =
        Thread.currentThread().contextClassLoader.getResourceAsStream("firestore-service-account-key.json")
    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://bridge-store.firebaseio.com")
        .build()
    FirebaseApp.initializeApp(options)

    return FirestoreClient.getFirestore()
}
