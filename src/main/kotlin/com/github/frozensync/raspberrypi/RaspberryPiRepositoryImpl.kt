package com.github.frozensync.raspberrypi

import com.google.cloud.firestore.Firestore
import mu.KotlinLogging

const val RASPBERRY_PI_COLLECTION = "raspberryPis"

class RaspberryPiRepositoryImpl(private val db: Firestore) : RaspberryPiRepository {

    private val logger = KotlinLogging.logger { }

    override fun save(entity: RaspberryPi) {
        logger.entry(entity)

        val data = mapOf("owned" to entity.owned)

        db.collection(RASPBERRY_PI_COLLECTION)
            .document(entity.id.toString())
            .create(data)

        logger.exit()
    }
}
