package com.github.frozensync.database

import com.google.api.gax.rpc.ApiException
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

/**
 * Returns the result of RPC call [block], executed in a [Dispatchers.IO] context, with exponential backoff on failure.
 *
 * If [block] throws an [ApiException], retries again after [initialDelay].
 * Subsequent retries are delayed by a factor of [factor] with an upper limit of [maxDelay].
 * After [times] retries, lets the thrown exception bubble upwards.
 */
suspend fun <T> Firestore.retry(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = 500, // milliseconds
    maxDelay: Long = 30000, // milliseconds
    factor: Double = 2.0,
    block: suspend Firestore.() -> T
): T = withContext(Dispatchers.IO) {
    logger.entry(times, initialDelay, maxDelay, factor)

    var currentDelay = initialDelay

    repeat(times - 1) {
        try {
            return@withContext block().also { logger.exit(it) }
        } catch (e: Exception) {
            logger.catching(e)
        }

        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)

        logger.warn { "Firestore request failed. Retrying in ${currentDelay / 1000} seconds. Attempt: ${it + 1}/$times." }
    }

    try {
        return@withContext block().also { logger.exit(it) }
    } catch (e: Exception) {
        logger.catching(e)
        throw e
    }
}
