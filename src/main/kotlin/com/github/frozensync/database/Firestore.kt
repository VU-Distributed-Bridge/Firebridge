package com.github.frozensync.database

import com.google.api.gax.rpc.ApiException
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.delay
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Returns the result of RPC call [block] with exponential backoff on failure.
 *
 * If [block] throws an [ApiException], retries again after [initialDelay].
 * Subsequent retries are delayed by a factor of [factor] with an upper limit of [maxDelay].
 * After [times] retries, lets the thrown exception bubble upwards.
 */
@Suppress("unused")
suspend fun <T> Firestore.retry(
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
