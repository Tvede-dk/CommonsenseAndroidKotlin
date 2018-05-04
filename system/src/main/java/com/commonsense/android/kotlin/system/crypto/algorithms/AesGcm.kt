package com.commonsense.android.kotlin.system.crypto.algorithms

import android.support.annotation.RequiresApi
import com.commonsense.android.kotlin.system.crypto.CipherText
import com.commonsense.android.kotlin.system.crypto.Ciphers
import com.commonsense.android.kotlin.system.crypto.Ciphers.getGcmAesCipher
import com.commonsense.android.kotlin.system.crypto.Random
import com.commonsense.android.kotlin.system.crypto.SymmetricEncryption
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Kasper Tvede on 11-04-2018.
 * Purpose:
 *
 */
@RequiresApi(19)
class AesGcm private constructor(
        private val gcmCipher: Cipher,
        private val secureRandom: SecureRandom,
        private val gcmTagLenght: GcmTagLenght,
        private val key: SecretKeySpec) : SymmetricEncryption {


    override fun decrypt(input: CipherText): ByteArray? {
        gcmCipher.init(Cipher.DECRYPT_MODE, key, generateGcmSpec(gcmTagLenght, input.iv))
        return gcmCipher.doFinal(input.cipherText)
    }

    override fun encrypt(input: ByteArray): CipherText? {
        val gcmSpec = generateGcmSpec(gcmTagLenght, null)
        gcmCipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)
        val cipherText = gcmCipher.doFinal(input) ?: return null
        return CipherText(cipherText, gcmCipher.iv)
    }

    /**
     *
     */
    private fun generateGcmSpec(tagLength: GcmTagLenght, tag: ByteArray?): GCMParameterSpec {
        val safeTag = tag ?: generateNonce(tagLength)
        return GCMParameterSpec(tagLength.tagLengthInBits, safeTag)
    }

    /**
     *
     */
    private fun generateNonce(tagLength: GcmTagLenght): ByteArray {
        return tagLength.generateNonce(secureRandom)
    }

    companion object {

        /***
         *
         */
        fun createGcmAesWith(rawAesKey: ByteArray,
                             gcmTagLenght: GcmTagLenght = GcmTagLenght.Bits128): AesGcm? {
            val cipher: Cipher = getGcmAesCipher() ?: return null
            val secretKey = SecretKeySpec(rawAesKey, "AES")
            val secureRandom = Random.getSha1SecureRandom() ?: return null
            return AesGcm(cipher, secureRandom, gcmTagLenght, secretKey)
        }

        /**
         *
         */
        fun createGcmAesNew(keySize: AesKeySize = AesKeySize.Bits128,
                            gcmTagLenght: GcmTagLenght = GcmTagLenght.Bits128): AesGcm? {
            val cipher: Cipher = Ciphers.getGcmAesCipher() ?: return null
            val secretKey = keySize.createNewKey() ?: return null
            val secureRandom = Random.getSha1SecureRandom() ?: return null
            return AesGcm(cipher, secureRandom, gcmTagLenght, secretKey)
        }

    }

}