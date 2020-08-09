package com.github.frozensync.tournament.raspberrypi

import com.github.frozensync.DeviceId

data class RaspberryPi(val id: DeviceId, val map: Map<String, Any?>) {
    val ownerId: String? by map
}
