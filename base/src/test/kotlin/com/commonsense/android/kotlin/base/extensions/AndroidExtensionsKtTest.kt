package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.*

/**
 *
 */
internal class AndroidExtensionsKtTest : BaseRoboElectricTest() {

    @org.junit.Test
    fun toEditable() {
        "".toEditable().apply {
            this.length.assert(0)
        }
        "test".toEditable().apply {
            this.length.assert(4)
            (this.toString() == "test").assert(true)
        }
    }
}