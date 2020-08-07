package com.github.frozensync.database

import org.koin.dsl.module

val firestoreModule = module {
    single { FirestoreFactory(get()).get() }
}
