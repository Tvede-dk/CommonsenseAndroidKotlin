package com.commonsense.android.base.extensions

import com.commonsense.android.kotlin.base.extensions.asUrl
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert

import org.junit.Test
import org.robolectric.annotation.Config

/**
 * Created by Kasper Tvede on 23-07-2017.
 */

@Config(manifest = Config.NONE)
class StringExtensionsKtTest : BaseRoboElectricTest() {
    @Test
    fun testAsUrl() {
        "google.com".asUrl(true).assert("https://google.com")
        "google.com".asUrl(false).assert("https://google.com")
        "http://google.com".asUrl(false).assert("http://google.com")
        "https://google.com".asUrl(false).assert("https://google.com")

    }

}