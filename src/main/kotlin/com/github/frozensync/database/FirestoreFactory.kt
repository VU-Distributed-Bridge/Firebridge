package com.github.frozensync.database

import com.github.frozensync.Configuration
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.File

/**
 * Factory which constructs [Firestore] instances.
 */
class FirestoreFactory(private val configuration: Configuration) {

    private val defaultFirebaseApp by lazy {
        val credentialsPath = configuration.googleCredentialsPath
        val credentials = GoogleCredentials.fromStream(File(credentialsPath).inputStream())
        val options = FirebaseOptions.Builder()
            .setCredentials(credentials)
            .setDatabaseUrl("https://bridge-store.firebaseio.com")
            .build()
        FirebaseApp.initializeApp(options)
    }

    /**
     * Returns a [Firestore] instance associated with the default Firebase app. Always returns the same instance.
     */
    fun get(): Firestore = FirestoreClient.getFirestore(defaultFirebaseApp)
}
