package com.github.frozensync.tournament.raspberrypi

/**
 * Service containing business logic operations on [RaspberryPi]s.
 */
interface RaspberryPiService {

    /**
     * Schedules a periodic health check with interval [delay] in milliseconds.
     */
    suspend fun scheduleHealthCheck(delay: Long = 20000L)

    /**
     * Preregisters this device if not already preregistered.
     */
    suspend fun register()
}
