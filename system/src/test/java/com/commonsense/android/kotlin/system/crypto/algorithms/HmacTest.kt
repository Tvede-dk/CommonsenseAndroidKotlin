package com.commonsense.android.kotlin.system.crypto.algorithms

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertNotNullApply
import org.junit.Test

/**
 * Created by Kasper Tvede on 20-04-2018.
 * Purpose:
 */
class HmacTest {

    @Test
    fun generateMac() {
        Hmac.createNew(HmacLength.Bits512).assertNotNullApply {
            val bytes = "some message".toByteArray(Charsets.UTF_8)
            val verifier = generateMac(bytes)
            val mac = this
            verifier.assertNotNullApply {
                val verifierMac = this
                mac.verifyMac(bytes, this).assert(true)

                val password = mac.encodedKey
                val newMac = Hmac.createWith(password)
                newMac.assertNotNullApply {
                    verifyMac(bytes, verifierMac).assert(true)
                }

                val fakePassword = password.copyOf()
                fakePassword[0] = (fakePassword[0] + 20).toByte()
                val fakeMac = Hmac.createWith(fakePassword)
                fakeMac.assertNotNullApply {
                    verifyMac(bytes, verifierMac).assert(false)
                }
            }
        }
    }
}