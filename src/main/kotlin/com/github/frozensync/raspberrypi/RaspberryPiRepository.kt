package com.github.frozensync.raspberrypi

/**
 * Interface for operations on a repository for [RaspberryPi].
 */
interface RaspberryPiRepository {

    /**
     * Saves the given [entity].
     */
    fun save(entity: RaspberryPi)
}
