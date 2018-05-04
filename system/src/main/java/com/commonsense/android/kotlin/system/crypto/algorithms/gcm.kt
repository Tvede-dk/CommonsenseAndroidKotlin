package com.commonsense.android.kotlin.system.crypto.algorithms

import java.security.SecureRandom

/**
 * Created by Kasper Tvede on 11-04-2018.
 */
/**
 * The GCM specification states that {@code tLen} may only have the
 * values {128, 120, 112, 104, 96},
 */
enum class GcmTagLenght(val tagLengthInBits: Int) {
    Bits128(128),
    Bits120(120),
    Bits112(112),
    Bits104(104),
    Bits96(96)
}

/**
 *
 */
fun GcmTagLenght.generateNonce(secureRandom: SecureRandom): ByteArray {
    val bytes = ByteArray(tagLengthInBits / 8)
    secureRandom.nextBytes(bytes)
    return bytes
}
