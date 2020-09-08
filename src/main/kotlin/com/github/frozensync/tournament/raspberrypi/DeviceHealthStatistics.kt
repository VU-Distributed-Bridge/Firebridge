package com.github.frozensync.tournament.raspberrypi

import java.util.concurrent.ConcurrentHashMap

class DeviceHealthStatistics(private val map: MutableMap<String, Any?> = ConcurrentHashMap()) {

    var amountOfScores: Long
        get() = map.getOrDefault("amountOfScores", 0L) as Long
        set(value) {
            map["amountOfScores"] = value
        }

    var batteryPercentage: Long?
        get() = map.getOrDefault("batteryPercentage", null) as Long?
        set(value) {
            map["batteryPercentage"] = value
        }

    var region: String?
        get() = map.getOrDefault("region", null) as String?
        set(value) {
            map["region"] = value
        }

    var channel: Long?
        get() = map.getOrDefault("channel", null) as Long?
        set(value) {
            map["channel"] = value
        }
}
