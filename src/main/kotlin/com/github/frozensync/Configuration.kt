package com.github.frozensync

import org.koin.core.Koin
import org.koin.ext.getIntProperty
import java.util.*

typealias DeviceId = UUID

private const val DEVICE_ID_KEY = "DEVICE_ID"
private const val GOOGLE_APPLICATION_CREDENTIALS_KEY = "GOOGLE_APPLICATION_CREDENTIALS"
private const val GRPC_SERVER_PORT = "GRPC_SERVER_PORT"

fun Koin.validateConfiguration(): String {
    val errorMessage = StringBuilder()

    getProperty(DEVICE_ID_KEY)
        ?.run {
            try {
                DeviceId.fromString(this)
            } catch (e: IllegalArgumentException) {
                errorMessage.appendln("DEVICE_ID does not conform to a UUID")
            }
        }
        ?: errorMessage.appendln("Missing environment variable: DEVICE_ID")

    getProperty(GOOGLE_APPLICATION_CREDENTIALS_KEY)
        ?: errorMessage.appendln("Missing environment variable: GOOGLE_APPLICATION_CREDENTIALS")

    return errorMessage.toString()
}

class Configuration(koin: Koin) {
    val deviceId = koin.getDeviceIdProperty(DEVICE_ID_KEY)!!
    val googleCredentialsPath = koin.getProperty(GOOGLE_APPLICATION_CREDENTIALS_KEY)!!
    val grpcServerPort = koin.getIntProperty(GRPC_SERVER_PORT) ?: 8980
}

private fun Koin.getDeviceIdProperty(key: String) = getProperty(key)?.let { DeviceId.fromString(it) }
