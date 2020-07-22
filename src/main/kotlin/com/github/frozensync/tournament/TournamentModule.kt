package com.github.frozensync.tournament

import org.koin.dsl.module

val tournamentModule = module {
    single<TournamentService> { TournamentServiceImpl(get(), get()) }
}
