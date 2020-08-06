package com.github.frozensync.tournament

import com.github.frozensync.Configuration
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging

class ScorerServer(configuration: Configuration, scorerService: ScorerService) {

    private val logger = KotlinLogging.logger { }

    private val port = configuration.grpcServerPort
    private val server = ServerBuilder.forPort(port)
        .addService(scorerService)
        .addService(ProtoReflectionService.newInstance())
        .build()

    fun start(): ScorerServer {
        server.start()
        logger.info { "gRPC server started, listening on $port" }
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
