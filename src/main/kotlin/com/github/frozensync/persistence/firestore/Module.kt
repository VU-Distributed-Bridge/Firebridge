package com.github.frozensync.persistence.firestore

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.koin.dsl.module

val firestoreModule = module {
    single {
        initFirebase()
        FirestoreClient.getFirestore()
    }
}

private fun initFirebase() =
    Thread.currentThread().contextClassLoader.getResourceAsStream("firestore-service-account-key.json").use {
        val credentials = GoogleCredentials.fromStream(it)
        val options = FirebaseOptions.Builder()
            .setCredentials(credentials)
            .setDatabaseUrl("https://bridge-store.firebaseio.com")
            .build()
        FirebaseApp.initializeApp(options)
    }
