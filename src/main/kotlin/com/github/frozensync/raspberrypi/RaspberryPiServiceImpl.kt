package com.github.frozensync.raspberrypi

import com.github.frozensync.Configuration
import com.github.frozensync.persistence.firestore.retry
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging

class RaspberryPiServiceImpl(private val configuration: Configuration, private val db: Firestore) : RaspberryPiService {

    private val logger = KotlinLogging.logger { }

    private val collection = db.collection("raspberryPis")
    private val selfRef = collection.document(configuration.deviceId.toString())

    private var healthCheckJob: Job? = null

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi // see https://github.com/Kotlin/kotlinx.coroutines/issues/540
    override suspend fun scheduleHealthCheck(delay: Long) {
        healthCheckJob?.cancel()

        healthCheckJob = GlobalScope.launch {
            ticker(delay).consumeEach {
                selfRef.update("healthCheck.latestPing", FieldValue.serverTimestamp()).get()
            }
        }

        logger.info { "Scheduled a health check with a ${delay / 1000}-seconds interval" }
    }

    override suspend fun register() = withContext(Dispatchers.IO) {
        val raspberryPi = RaspberryPi(configuration.deviceId)
        val data = mapOf("owned" to raspberryPi.owned)

        logger.info { "Preregistering the device..." }
        with(db) {
            val docRef = collection.document(raspberryPi.id.toString())
            val docFuture = docRef.get()
            val doc = docFuture.get()

            if (doc.exists()) {
                logger.info { "Device is already preregistered." }
                return@withContext
            }

            retry {
                withContext(Dispatchers.IO) { docRef.create(data).get() }
            }
            logger.info { "Device is successfully preregistered." }

        }
    }
}
