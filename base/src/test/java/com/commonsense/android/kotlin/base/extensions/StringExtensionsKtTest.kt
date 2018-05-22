package com.commonsense.android.kotlin.base.extensions

import android.net.Uri
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertNotNullAndEquals
import com.commonsense.android.kotlin.test.assertNull
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

    @Test
    fun testFileExtension() {
        val noExtensionMessage = "there are no extensions in this string"
        "".fileExtension().assertNull(noExtensionMessage)
        "test".fileExtension().assertNull(noExtensionMessage)
        "test.".fileExtension().assertNull(noExtensionMessage)
        "test..".fileExtension().assertNull(noExtensionMessage)

        "test.a".fileExtension().assertNotNullAndEquals("a")
        "test.a.".fileExtension().assertNull("since the text ends in . then there are no extension.")
        "test.a.b".fileExtension().assertNotNullAndEquals("b")
        "test..a".fileExtension().assertNotNullAndEquals("a")

        //more real life examples
        "test.xml".fileExtension().assertNotNullAndEquals("xml")
        "test.\$java".fileExtension().assertNotNullAndEquals("\$java")
        "test.\"xml".fileExtension().assertNotNullAndEquals("\"xml")

    }

    @Test
    fun testUriExtension() {
        Uri.parse("https://test.test").fileExtension().assertNull()
        Uri.parse("https://test.test/test.xml").fileExtension().assertNotNullAndEquals("xml")
        Uri.parse("https://test.test/page.html").fileExtension().assertNotNullAndEquals("html")
        Uri.parse("https://test.test/page.aspx").fileExtension().assertNotNullAndEquals("aspx")
        Uri.parse("https://test.test/test/test.test/test.2").fileExtension().assertNotNullAndEquals("2")
    }

    @Test
    fun testWithoutQueryParameters() {
        val simple = Uri.parse("https://test.test")
        simple.withoutQueryParameters().assert(simple)
        val simpleQuery = simple.buildUpon().query("test=test").build()
        simpleQuery.withoutQueryParameters().assert(simple)
    }

}