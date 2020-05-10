package com.github.frozensync.persistence.firestore

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.koin.core.KoinComponent
import java.io.File

/**
 * Factory which constructs [Firestore] instances.
 */
class FirestoreFactory : KoinComponent {

    private var initialized = false

    /**
     * Returns a [Firestore] instance associated with the default Firebase app. Always returns the same instance.
     */
    fun get(): Firestore {
        if (!initialized) {
            initializeDefaultFirebaseApp()
            initialized = true
        }
        return FirestoreClient.getFirestore()
    }

    private fun initializeDefaultFirebaseApp() {
        val credentialsPath = getKoin().getProperty<String>("GOOGLE_APPLICATION_CREDENTIALS")!!
        val credentials = GoogleCredentials.fromStream(File(credentialsPath).inputStream())
        val options = FirebaseOptions.Builder()
            .setCredentials(credentials)
            .setDatabaseUrl("https://bridge-store.firebaseio.com")
            .build()
        FirebaseApp.initializeApp(options)
    }
}
