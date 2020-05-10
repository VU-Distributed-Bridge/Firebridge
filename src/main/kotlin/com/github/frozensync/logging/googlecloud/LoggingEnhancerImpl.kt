package com.github.frozensync.logging.googlecloud

import com.google.cloud.logging.LogEntry
import com.google.cloud.logging.LoggingEnhancer
import org.koin.core.KoinComponent

class LoggingEnhancerImpl : LoggingEnhancer, KoinComponent {
    override fun enhanceLogEntry(builder: LogEntry.Builder) {
        val id = getKoin().getProperty<String>("RASPBERRY_PI_ID")

        builder.addLabel("component", "raspberry-pi")
        builder.addLabel("component-id", id)
    }
}
