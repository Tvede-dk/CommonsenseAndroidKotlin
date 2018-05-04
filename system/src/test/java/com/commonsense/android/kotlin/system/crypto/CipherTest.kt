package com.commonsense.android.kotlin.system.crypto

import com.commonsense.android.kotlin.system.crypto.algorithms.AesKeySize
import com.commonsense.android.kotlin.system.crypto.algorithms.HmacLength
import com.commonsense.android.kotlin.test.*
import org.junit.Test
import org.robolectric.annotation.Config
import javax.crypto.AEADBadTagException

/**
 * Created by Kasper Tvede on 08-04-2018.
 * Purpose:
 */
@Config(sdk = [16, 17, 18, 21, 25])
class CipherTest : BaseRoboElectricTest() {

    @Test
    fun testAesCbc() {
        val toEncrypt = "omg this is swag"
        val workingCipher = Crypto.createAesCbcHmacNew(AesKeySize.Bits256, HmacLength.Bits512)
        if (workingCipher == null) {
            failTest("could not get cipher.")
            return
        }
        val cipherText = workingCipher.encrypt(toEncrypt.toByteArray(kotlin.text.Charsets.UTF_8))
        cipherText.assertNotNullApply {
            this.iv.assertNotNullApply {
                this.size.assert(128 / 8) //default should be most secure. which is 128 bit tags.
            }
            this.cipherText.assertNotNullApply {
                this.size.assertLargerThan(0, "should have ciphertext values")
            }
            val decrypted = workingCipher.decrypt(this)
            decrypted.assertNotNullApply {
                this.toString(kotlin.text.Charsets.UTF_8).assert(toEncrypt, "should have all decrypted ")
            }

        }
        cipherText.assertNotNullApply {
            //try tamper with it.
            this.iv[0] = this.iv[0].plusUnsafe(1)
            assertThrows<AEADBadTagException> {
                val decrypted = workingCipher.decrypt(this)
                decrypted.assertNull("cannot decrypt when providing wrong iv.")
            }

        }
    }
}


/**
 * as the name implies this is unsafe, as we can overflow or underflow.
 * ignoring the underflow / overflow
 */
fun Byte.plusUnsafe(other: Byte): Byte {
    return (this + other).toByte()
}