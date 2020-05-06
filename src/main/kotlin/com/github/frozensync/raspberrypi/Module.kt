package com.github.frozensync.raspberrypi

import org.koin.dsl.module

val raspberryPiModule = module {
    single<RaspberryPiService> { RaspberryPiServiceImpl(get()) }
    single<RaspberryPiRepository> { RaspberryPiRepositoryImpl(get()) }
}
