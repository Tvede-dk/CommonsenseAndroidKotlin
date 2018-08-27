package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assert
import org.junit.*
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 15-04-2018.
 * Purpose:
 */
class CharExtensionsKtTest {

    @Test
    fun toCase() {
        'a'.toCase(true).assert('A')
        'a'.toCase(false).assert('a')

        'A'.toCase(true).assert('A')
        'A'.toCase(false).assert('a')

        '0'.toCase(true).assert('0')
        '0'.toCase(false).assert('0')
    }

    @Ignore
    @Test
    fun hexCharsToByte() {
    }

    @Ignore
    @Test
    fun mapFromHexValue() {
    }
}