package com.github.frozensync.tournament.raspberrypi

import java.util.concurrent.ConcurrentHashMap

class DeviceHealthStatistics(private val map: MutableMap<String, Any?> = ConcurrentHashMap()) {

    var amountOfScores: Long
        get() = map.getOrDefault("amountOfScores", 0L) as Long
        set(value) {
            map["amountOfScores"] = value
        }
}
