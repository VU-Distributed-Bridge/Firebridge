package com.github.frozensync.persistence.firestore

import org.koin.dsl.module

val firestoreModule = module {
    single { FirestoreFactory(get()).get() }
}
