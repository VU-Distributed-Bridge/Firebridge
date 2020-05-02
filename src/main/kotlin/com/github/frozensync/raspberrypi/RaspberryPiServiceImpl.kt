package com.github.frozensync.raspberrypi

import mu.KotlinLogging
import java.util.*

class RaspberryPiServiceImpl(private val repository: RaspberryPiRepository) : RaspberryPiService {

    private val logger = KotlinLogging.logger { }

    override fun register(id: UUID) {
        logger.entry(id)

        val pi = RaspberryPi(id)
        repository.save(pi)

        logger.exit()
    }
}
