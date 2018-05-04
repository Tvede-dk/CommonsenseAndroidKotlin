package com.commonsense.android.kotlin.system.crypto

import android.support.annotation.RequiresApi
import com.commonsense.android.kotlin.system.crypto.algorithms.*
import com.commonsense.android.kotlin.system.extensions.ifApiIsEqualOrGreater
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey


/**
 * Created by Kasper Tvede on 07-04-2018.
 * Purpose: both wrapping and making it a lot easier to perform crypto using kotlin and in android.
 * also avoids the broken / bad ciphers / insecure as theses should not be expressed;
 * if required, then its properly best if its hard to use such that people will understand how, when ,why its a bad cipher.
 */
object Ciphers {

    /**
     * Aes / GCM cipher.
     * should only be used on api >= 19 as you cannot use it at lower api's even though its
     * available at api 10+.
     * so due to the issues with using it below 19 we target it for 19
     */
    @RequiresApi(19)
    fun getGcmAesCipher(): Cipher? = Cipher.getInstance("AES/GCM/NoPadding")

    /**
     * the most standard of aes. CBC mode with PKCS5 padding (check out the java and padding, since theres a lot of confusion about PKCS7 and PKCS5 padding).
     *
     */
    fun getAesCbc5Padding(): Cipher? = Cipher.getInstance("AES/CBC/PKCS5Padding")


}

/**
 *
 */
object Macs {
    /**
     *
     */
    fun getHmacSha256(): Mac? = Mac.getInstance("HmacSHA256")

    /**
     *
     */
    fun getHmacSha512(): Mac? = Mac.getInstance("HmacSHA512")

}

object Hashes {

}

/**
 *
 *
 * @see https://docs.oracle.com/javase/7/docs/api/javax/crypto/KeyGenerator.html
 */
object KeyGenerators {
    /**
     *
     */
    fun getAesKeyGenerator(): KeyGenerator? = KeyGenerator.getInstance("AES")

    fun getHmac256KeyGenerator(): KeyGenerator? = KeyGenerator.getInstance("HmacSHA256")

    fun getHmac1KeyGenerator(): KeyGenerator? = KeyGenerator.getInstance("HmacSHA1")


}

object Random {

    /**
     *
     * Based on
     * https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-131Ar1.pdf
     * its okay to use sha1 to generate random numbers. ( page 15)
     */
    fun getSha1SecureRandom(): SecureRandom? = SecureRandom.getInstance("SHA1PRNG")
}


object Crypto {

    object SymmetricWithAuthentication {
        /**
         * Either creates the AesGcm iff available, or create the aes cbc hmac.
         *
         *
         */
        fun createMostSecure(): SymmetricEncryption? {
            return ifApiIsEqualOrGreater(19) {
                //GCMParameterSpec is only available from 19 and onwards.
                AesGcm.createGcmAesNew(AesKeySize.Bits256, GcmTagLenght.Bits128)
                //else if not available or working , use aes cbc + hmac
            } ?: createAesCbcHmacNew(AesKeySize.Bits256, HmacLength.Bits512)
        }
    }


    fun createAesCbcHmacNew(aesKeySize: AesKeySize, macSize: HmacLength): AesCBCWithHmac? {
        val secretKeyAes = aesKeySize.createNewKey() ?: return null
        val confidentialityKey = macSize.createNewKey() ?: return null
        val aesCipher = Ciphers.getAesCbc5Padding() ?: return null
        val hmac = macSize.getMacFunction() ?: return null
        return AesCBCWithHmac(SecretKeys(secretKeyAes, confidentialityKey), aesCipher, hmac)
    }


}

/**
 *
 */
data class CipherText(
        val cipherText: ByteArray,
        val iv: ByteArray) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CipherText

        if (!Arrays.equals(cipherText, other.cipherText)) return false
        if (!Arrays.equals(iv, other.iv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(cipherText)
        result = 31 * result + Arrays.hashCode(iv)
        return result
    }
}

interface SymmetricEncryption {
    fun encrypt(input: ByteArray): CipherText?
    fun decrypt(input: CipherText): ByteArray?
}


data class SecretKeys(val encryptionKey: SecretKey, val confidentialityKey: SecretKey)
