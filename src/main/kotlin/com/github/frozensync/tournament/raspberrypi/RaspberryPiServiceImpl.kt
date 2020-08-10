package com.github.frozensync.tournament.raspberrypi

import com.github.frozensync.Configuration
import com.github.frozensync.DeviceId
import com.github.frozensync.database.retry
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging

class RaspberryPiServiceImpl(
    private val configuration: Configuration,
    private val db: Firestore,
    private val healthStatistics: DeviceHealthStatistics
) : RaspberryPiService {

    private val logger = KotlinLogging.logger { }

    private val collection = db.collection("raspberryPis")
    private val selfRef = collection.document(configuration.deviceId.toString())

    private var healthCheckJob: Job? = null

    override suspend fun getOwnerIdAsync(deviceId: DeviceId): Deferred<String> {
        logger.entry(deviceId)

        val result = CompletableDeferred<String>()

        val listener = collection.document(deviceId.toString())
            .addSnapshotListener listener@{ value, error ->
                if (error != null) {
                    result.completeExceptionally(error)
                    return@listener logger.catching(error)
                }
                if (value == null) return@listener logger.error { "Device document {id: $deviceId} not found" }

                val ownerId = value["ownerId"] as String?
                ownerId
                    ?.let { result.complete(it) }
                    ?: logger.debug { "Device $deviceId has no owner" }
            }

        result.invokeOnCompletion { listener.remove() }

        logger.exit()
        return result
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun register(deviceId: DeviceId) = withContext(Dispatchers.IO) {
        logger.info { "Registering device for discovery" }

        val docRef = collection.document(deviceId.toString())
        val docFuture = docRef.get()
        val doc = docFuture.get()

        if (doc.exists()) {
            logger.info { "Device is already registered for discovery" }
        } else {
            val data = mapOf<String, Any?>("ownedId" to null)
            db.retry { docRef.create(data).get() }

            logger.info { "Device is successfully registered for discovery" }
        }
    }

    @ObsoleteCoroutinesApi // see https://github.com/Kotlin/kotlinx.coroutines/issues/540
    override suspend fun scheduleHealthCheck(delay: Long) {
        logger.entry(delay)

        healthCheckJob?.cancel()
        healthCheckJob = GlobalScope.launch {
            ticker(delay).consumeEach {
                selfRef.update(
                    "healthCheck.latestPing", FieldValue.serverTimestamp(),
                    "healthCheck.amountOfScores", healthStatistics.amountOfScores
                ).get()
            }
        }

        logger.info { "Scheduled a health check with a ${delay / 1000}-seconds interval" }
        logger.exit()
    }
}
