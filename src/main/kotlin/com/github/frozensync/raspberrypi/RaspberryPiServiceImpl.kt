package com.github.frozensync.raspberrypi

import com.github.frozensync.persistence.firestore.retry
import com.google.cloud.firestore.Firestore
import mu.KotlinLogging
import java.util.*

private const val RASPBERRY_PI_COLLECTION = "raspberryPis"

class RaspberryPiServiceImpl(private val db: Firestore) : RaspberryPiService {

    private val logger = KotlinLogging.logger { }

    override suspend fun register(id: UUID) {
        logger.entry(id)

        val raspberryPi = RaspberryPi(id)
        val data = mapOf("owned" to raspberryPi.owned)

        db.collection(RASPBERRY_PI_COLLECTION)
            .document(raspberryPi.id.toString())
            .create(data)

        logger.exit()
    }
}
