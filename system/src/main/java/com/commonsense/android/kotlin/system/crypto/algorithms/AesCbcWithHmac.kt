package com.commonsense.android.kotlin.system.crypto.algorithms

import com.commonsense.android.kotlin.system.crypto.*
import javax.crypto.Cipher
import javax.crypto.Mac

/**
 * Created by Kasper Tvede on 11-04-2018.
 * Purpose:
 *
 */
class AesCBCWithHmac(val keys: SecretKeys,
                     val cipher: Cipher,
                     val hmac: Mac) : SymmetricEncryption {


    override fun encrypt(input: ByteArray): CipherText? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decrypt(input: CipherText): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun createWith(keys: SecretKeys): AesCBCWithHmac? {
            val aesCipher: Cipher = Ciphers.getAesCbc5Padding() ?: return null
            val hmac: Mac = Macs.getHmacSha512() ?: return null

            return AesCBCWithHmac(keys, aesCipher, hmac)
        }
    }


}
