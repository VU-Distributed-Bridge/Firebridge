package com.github.frozensync.logging.googlecloud

import com.github.frozensync.Configuration
import com.google.cloud.logging.LogEntry
import com.google.cloud.logging.LoggingEnhancer
import org.koin.core.KoinComponent
import org.koin.core.get

class LoggingEnhancerImpl : LoggingEnhancer, KoinComponent {

    private val configuration: Configuration = get()

    override fun enhanceLogEntry(builder: LogEntry.Builder) {
        val id = configuration.deviceId.toString()

        builder.addLabel("component", "raspberry-pi")
        builder.addLabel("component-id", id)
    }
}
