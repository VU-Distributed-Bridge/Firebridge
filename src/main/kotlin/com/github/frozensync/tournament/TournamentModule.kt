package com.github.frozensync.tournament

import com.github.frozensync.RASPBERRY_PI_ID
import org.koin.dsl.module

val tournamentModule = module {
    single<TournamentService> { TournamentServiceImpl(get(), getProperty(RASPBERRY_PI_ID)) }
}
