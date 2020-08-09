package com.github.frozensync.tournament.raspberrypi

import com.github.frozensync.DeviceId
import kotlinx.coroutines.Deferred

/**
 * Service containing business logic operations on [RaspberryPi]s.
 */
interface RaspberryPiService {

    /**
     * Returns the id of the owner who owns the device [deviceId].
     */
    suspend fun getOwnerIdAsync(deviceId: DeviceId): Deferred<String>

    /**
     * Registers the device [deviceId] for discovery.
     *
     * Registration for discovery enables directors to find the device and claim ownership.
     */
    suspend fun register(deviceId: DeviceId)

    /**
     * Schedules a periodic health check with interval [delay] in milliseconds.
     */
    suspend fun scheduleHealthCheck(delay: Long = 20000L)
}
