package com.github.frozensync.tournament

import com.github.frozensync.tournament.ControlProgramGrpcKt.ControlProgramCoroutineStub
import io.grpc.ManagedChannel
import kotlinx.coroutines.coroutineScope
import java.io.Closeable
import java.util.concurrent.TimeUnit

class ControlProgramClient constructor(private val channel: ManagedChannel) : Closeable {

    private val stub: ControlProgramCoroutineStub = ControlProgramCoroutineStub(channel)

    suspend fun sendScore(score: Score) = coroutineScope {
        val request = ScoreRequest.newBuilder().setScore(score.result).build()
        stub.sendScore(request)
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
