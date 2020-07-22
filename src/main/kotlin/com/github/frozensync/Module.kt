package com.github.frozensync

import org.koin.dsl.module

val mainModule = module {
    single { Configuration(getKoin()) }
}
