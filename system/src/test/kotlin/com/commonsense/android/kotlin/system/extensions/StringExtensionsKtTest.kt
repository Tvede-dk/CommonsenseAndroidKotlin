package com.commonsense.android.kotlin.system.extensions

import com.commonsense.android.kotlin.test.*
import org.junit.*

/**
 * Created by Kasper Tvede on 30-05-2018.
 * Purpose:
 */
class StringExtensionsKtTest {

    @Test
    fun urlEncoded() {
        "".urlEncoded().assert("")

        "a".urlEncoded().assert("a")

        " ".urlEncoded().assert("+")
        "%".urlEncoded().assert("%25")


    }
}