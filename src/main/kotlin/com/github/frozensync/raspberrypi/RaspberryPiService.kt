package com.github.frozensync.raspberrypi

import java.util.*

/**
 * Interface for business logic operations on [RaspberryPi]s.
 */
interface RaspberryPiService {

    /**
     * Registers a new raspberry pi with given [id]. If it's already registered, fail silently.
     */
    fun register(id: UUID)
}
