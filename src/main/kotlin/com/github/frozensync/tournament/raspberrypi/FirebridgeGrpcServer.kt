package com.github.frozensync.tournament.raspberrypi

import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging

class FirebridgeGrpcServer(port: Int, firebridgeGrpcService: FirebridgeGrpcService) {

    private val logger = KotlinLogging.logger { }

    private val server = ServerBuilder.forPort(port)
        .addService(firebridgeGrpcService)
        .addService(ProtoReflectionService.newInstance())
        .build()

    fun start(): FirebridgeGrpcServer {
        server.start()
        logger.info { "gRPC server started, listening on ${server.port}" }
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.debug { "*** shutting down gRPC server since JVM is shutting down" }
                this@FirebridgeGrpcServer.stop()
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
