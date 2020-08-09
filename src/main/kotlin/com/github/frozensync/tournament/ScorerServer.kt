package com.github.frozensync.tournament

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging

class ScorerServer(port: Int, scorerService: ScorerService) {

    private val logger = KotlinLogging.logger { }

    private val server = ServerBuilder.forPort(port)
        .addService(scorerService)
        .addService(ProtoReflectionService.newInstance())
        .build()

    fun start(): ScorerServer {
        server.start()
        logger.info { "gRPC server started, listening on ${server.port}" }
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.debug { "*** shutting down gRPC server since JVM is shutting down" }
                this@ScorerServer.stop()
                logger.debug { "*** server shut down" }
            }
        )
        return this
    }

    fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
