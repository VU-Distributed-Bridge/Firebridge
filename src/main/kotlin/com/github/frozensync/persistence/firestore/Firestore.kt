package com.github.frozensync.persistence.firestore

import com.google.api.gax.rpc.ApiException
import kotlinx.coroutines.delay
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

suspend fun <T> retry(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = 500, // milliseconds
    maxDelay: Long = 30000, // milliseconds
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    logger.entry(times, initialDelay, maxDelay, factor)

    var currentDelay = initialDelay

    repeat(times - 1) {
        try {
            return block().also { logger.exit(it) }
        } catch (e: ApiException) {
            logger.catching(e)
        }

        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)

        logger.warn { "Firestore request failed. Retrying in ${currentDelay / 1000} seconds. Attempt: ${it + 1}/$times." }
    }

    return block().also { logger.exit(it) }
}
