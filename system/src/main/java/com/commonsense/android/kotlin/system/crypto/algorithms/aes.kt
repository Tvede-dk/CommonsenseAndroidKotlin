package com.commonsense.android.kotlin.system.crypto.algorithms

import com.commonsense.android.kotlin.system.crypto.KeyGenerators
import com.commonsense.android.kotlin.system.crypto.Random
import com.commonsense.android.kotlin.system.logging.tryAndLog
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Kasper Tvede on 11-04-2018.
 * Purpose:
 *
 */


/**
 * Creates a new aes key, given the seed.
 */
fun AesKeySize.createNewKey(seed: Long = System.nanoTime()): SecretKeySpec? =
        tryAndLog("AesKeySize.createNewKey",
                "failed at creating a private key.") {
            val keyGenerator = KeyGenerators.getAesKeyGenerator()
            val sr = Random.getSha1SecureRandom()
            sr?.setSeed(seed)
            keyGenerator?.init(sizeInBits, sr)
            SecretKeySpec(keyGenerator?.generateKey()?.encoded, "AES")
        }


enum class AesKeySize(val sizeInBits: Int) {
    Bits128(128),
    Bits192(192),
    Bits256(256),
}
