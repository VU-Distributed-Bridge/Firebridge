package com.github.frozensync

import org.koin.core.Koin
import java.util.*

typealias DeviceId = UUID

private const val DEVICE_ID_KEY = "DEVICE_ID"
private const val GOOGLE_APPLICATION_CREDENTIALS_KEY = "GOOGLE_APPLICATION_CREDENTIALS"

fun Koin.validateConfiguration(): String {
    val errorMessage = StringBuilder()

    getProperty<String>(DEVICE_ID_KEY)
        ?.let { this.setProperty("DEVICE_ID", DeviceId.fromString(it)) }
        ?: errorMessage.appendln("Missing environment variable: DEVICE_ID")

    getProperty<String>(GOOGLE_APPLICATION_CREDENTIALS_KEY)
        ?: errorMessage.appendln("Missing environment variable: GOOGLE_APPLICATION_CREDENTIALS")

    return errorMessage.toString()
}

class Configuration(koin: Koin) {
    val deviceId = koin.getProperty<DeviceId>(DEVICE_ID_KEY)!!
    val googleCredentialsPath = koin.getProperty<String>(GOOGLE_APPLICATION_CREDENTIALS_KEY)!!
}
