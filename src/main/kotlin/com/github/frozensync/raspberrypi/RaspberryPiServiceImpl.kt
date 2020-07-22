package com.github.frozensync.raspberrypi

import com.github.frozensync.Configuration
import com.github.frozensync.persistence.firestore.retry
import com.google.cloud.firestore.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging

private const val RASPBERRY_PI_COLLECTION = "raspberryPis"

class RaspberryPiServiceImpl(private val configuration: Configuration, private val db: Firestore) : RaspberryPiService {

    private val logger = KotlinLogging.logger { }

    override suspend fun register() = withContext(Dispatchers.IO) {
        val raspberryPi = RaspberryPi(configuration.deviceId)
        val data = mapOf("owned" to raspberryPi.owned)

        logger.info { "Preregistering the device..." }
        with(db) {
            val docRef = collection(RASPBERRY_PI_COLLECTION).document(raspberryPi.id.toString())
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
