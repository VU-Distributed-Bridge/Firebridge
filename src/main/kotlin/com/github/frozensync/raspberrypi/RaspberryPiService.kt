package com.github.frozensync.raspberrypi

/**
 * Service containing business logic operations on [RaspberryPi]s.
 */
interface RaspberryPiService {

    /**
     * Preregisters this device if not already preregistered.
     */
    suspend fun register()
}
