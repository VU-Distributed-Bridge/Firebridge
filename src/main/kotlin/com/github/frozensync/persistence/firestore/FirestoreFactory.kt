package com.github.frozensync.persistence.firestore

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient

/**
 * Factory which constructs a single [Firestore] instance for the lifetime of the program.
 */
object FirestoreFactory {

    private val firestore: Firestore

    init {
        firestore =
            Thread.currentThread().contextClassLoader.getResourceAsStream("firestore-service-account-key.json").use {
                val credentials = GoogleCredentials.fromStream(it)
                val options = FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setDatabaseUrl("https://bridge-store.firebaseio.com")
                    .build()

                FirebaseApp.initializeApp(options)
                FirestoreClient.getFirestore()
            }
    }

    /**
     * Returns a singleton [Firestore] instance associated with the default Firebase app.
     */
    fun get() = firestore
}
