package com.commonsense.android.kotlin.system.crypto

import android.os.Build
import android.provider.Settings
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException

/**
 * Created by Kasper Tvede on 10-04-2018.
 * Purpose:
 *
 */
/**
 * Gets the hardware serial number of this device.
 *
 * @return serial number or `null` if not available.
 */
internal fun getDeviceAndroidId(): String? {
    return Settings.Secure.ANDROID_ID

}

internal fun getBuildFingerprintAndDeviceSerial(): ByteArray {
    val result = StringBuilder()
    val fingerprint = Build.FINGERPRINT
    if (fingerprint != null) {
        result.append(fingerprint)
    }
    val serial = getDeviceAndroidId()
    if (serial != null) {
        result.append(serial)
    }
    try {
        return result.toString().toByteArray(charset("UTF-8"))
    } catch (e: UnsupportedEncodingException) {
        throw RuntimeException("UTF-8 encoding not supported")
    }

}

/**
 * Generates a device- and invocation-specific seed to be mixed into the
 * Linux PRNG.
 */
internal fun generateSeed(): ByteArray {
    try {
        val seedBuffer = ByteArrayOutputStream()
        val seedBufferOut = DataOutputStream(seedBuffer)
        seedBufferOut.writeLong(System.currentTimeMillis())
        seedBufferOut.writeLong(System.nanoTime())
        seedBufferOut.writeInt(android.os.Process.myPid())
        seedBufferOut.writeInt(android.os.Process.myUid())
        seedBufferOut.write(deviceFingerprint)
        seedBufferOut.close()
        return seedBuffer.toByteArray()
    } catch (e: IOException) {
        throw SecurityException("Failed to generate seed", e)
    }
}

/**
 * Used here and for the linux RPNGSecureRandom.kt
 */
private val deviceFingerprint by lazy {
    getBuildFingerprintAndDeviceSerial()
}
