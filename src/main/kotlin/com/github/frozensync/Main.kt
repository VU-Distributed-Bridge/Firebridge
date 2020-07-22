package com.github.frozensync

import com.github.frozensync.persistence.firestore.firestoreModule
import com.github.frozensync.raspberrypi.RaspberryPiService
import com.github.frozensync.raspberrypi.raspberryPiModule
import com.github.frozensync.tournament.tournamentModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)

    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun main(): Unit = runBlocking {
    val koin = startKoin {
        modules(mainModule, firestoreModule, raspberryPiModule, tournamentModule)
        environmentProperties()
    }.koin
    val errorMessage = koin.validateConfiguration()
    if (errorMessage.isNotEmpty()) {
        System.err.println(errorMessage)
        exitProcess(1)
    }

    val raspberryPiService = koin.get<RaspberryPiService>()
    raspberryPiService.register()

    logger.info { "Started FireBridge" }
    embeddedServer(Netty, 8080, module = Application::module).start()
    return@runBlocking
}
