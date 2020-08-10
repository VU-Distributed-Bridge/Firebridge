package com.github.frozensync.tournament

import io.grpc.Server
import io.grpc.ServerBuilder

/**
 * A mock of a BridgeMate server connected to the control program. Its purpose is to verify the RPC communication.
 */
private class ControlProgramMockServer constructor(private val port: Int) {

    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(ControlProgramService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")

        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@ControlProgramMockServer.stop()
                println("Server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class ControlProgramService : ControlProgramGrpcKt.ControlProgramCoroutineImplBase() {
        override suspend fun sendScore(request: ScoreRequest): Empty {
            println(request)
            return Empty.getDefaultInstance()
        }
    }
}

fun main() {
    val server = ControlProgramMockServer(port = 8981)
    server.start()
    server.blockUntilShutdown()
}
