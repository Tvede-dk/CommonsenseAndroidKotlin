package com.commonsense.android.kotlin.system.crypto.algorithms

import com.commonsense.android.kotlin.base.EmptyFunctionResult
import com.commonsense.android.kotlin.system.crypto.Macs
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


/**
 * Created by Kasper Tvede on 11-04-2018.
 * Purpose:
 *
 */

enum class HmacLength(val sizeInBits: Int,
                      val getMacFunction: EmptyFunctionResult<Mac?>) {
    Bits256(256,
            Macs::getHmacSha256),

    Bits512(512,
            Macs::getHmacSha512);

    val sizeInBytes
        get() = sizeInBits / 8


    companion object {
        fun from(rawKey: ByteArray): HmacLength? {
            return when (rawKey.size) {
                Bits256.sizeInBytes -> Bits256
                Bits512.sizeInBytes -> Bits512
                else -> null
            }
        }
    }

    val hmacAlgorithmName: String
        get() = "HMACSHA$sizeInBits"
}


fun HmacLength.createNewKey(): SecretKey? {
    val kgen = KeyGenerator.getInstance(hmacAlgorithmName) ?: return null
    val key = kgen.generateKey() ?: return null
    return key
}

class Hmac private constructor(
        private val mac: Mac,
        private val signingKey: SecretKey) {


    val encodedKey: ByteArray
        get () = signingKey.encoded

    companion object {
        fun createNew(length: HmacLength): Hmac? {
            val mac = length.getMacFunction() ?: return null
            val key = length.createNewKey() ?: return null
            return Hmac(mac, key)
        }

        fun createWith(rawKey: ByteArray): Hmac? {
            val hmacLength = HmacLength.from(rawKey) ?: return null
            val key = SecretKeySpec(rawKey, hmacLength.hmacAlgorithmName)
            val mac = hmacLength.getMacFunction() ?: return null
            return Hmac(mac, key)
        }
    }

    fun generateMac(data: ByteArray): ByteArray? {
        mac.init(signingKey)
        mac.update(data)
        return mac.doFinal()
    }

    fun verifyMac(data: ByteArray, mac: ByteArray): Boolean {
        val newMac = generateMac(data)
        return newMac?.contentEquals(mac) ?: return false
    }

}