package com.commonsense.android.kotlin.base.extensions

import android.net.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.robolectric.annotation.*

/**
 * Created by Kasper Tvede on 23-07-2017.
 */

@Config(manifest = Config.NONE)
class StringExtensionsKtTest : BaseRoboElectricTest() {


    @Test
    fun testUriExtension() {
        Uri.parse("https://test.test").fileExtension().assertNull()
        Uri.parse("https://test.test/test.xml").fileExtension().assertNotNullAndEquals("xml")
        Uri.parse("https://test.test/page.html").fileExtension().assertNotNullAndEquals("html")
        Uri.parse("https://test.test/page.aspx").fileExtension().assertNotNullAndEquals("aspx")
        Uri.parse("https://test.test/test/test.test/test.2").fileExtension().assertNotNullAndEquals("2")
    }

    @Test
    fun asUrl() {
        "google.com".asUrl(true).assert("https://google.com")
        "google.com".asUrl(false).assert("https://google.com")
        "http://google.com".asUrl(false).assert("http://google.com")
        "https://google.com".asUrl(false).assert("https://google.com")
    }

    @Test
    fun fileExtension() {
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
    fun withoutQueryParameters() {
        val simple = Uri.parse("https://test.test")
        simple.withoutQueryParameters().assert(simple)
        val simpleQuery = simple.buildUpon().query("test=test").build()
        simpleQuery.withoutQueryParameters().assert(simple)
    }

    @Test
    fun wrapInQuotes() {
        "".wrapInQuotes().assert("\"\"")

        "xyz".wrapInQuotes().assert("\"xyz\"")

        "\"something with qoutes\"".wrapInQuotes().assert("\"\"something with qoutes\"\"")
    }

    @Test
    fun fromHexStringToByteArray() {
        "aa".fromHexStringToByteArray().assertNotNullApply {
            size.assert(1)
            first().assert(0xaa)
        }
        "0x30".fromHexStringToByteArray().assertNotNullApply {
            count().assert(1)
            first().assert(0x30)
        }
        "30".fromHexStringToByteArray().assertNotNullApply {
            count().assert(1)
            first().assert(0x30)
        }

        "30203020".fromHexStringToByteArray().assertNotNullApply {
            count().assert(4)
            first().assert(0x30)
            get(1).assert(0x20)
            get(2).assert(0x30)
            last().assert(0x20)
        }
        " ".fromHexStringToByteArray().assertNull()
        "20x".fromHexStringToByteArray().assertNull()
        "qq".fromHexStringToByteArray().assertNull()
        "aa".fromHexStringToByteArray().assertNotNullApply {
            size.assert(1)
            first().assert(0xaa)
        }
        "FF".fromHexStringToByteArray().assertNotNullApply {
            size.assert(1)
            first().assert(0xFF)
        }
        "ff".fromHexStringToByteArray().assertNotNullApply {
            size.assert(1)
            first().assert(0xFF)
        }
    }

    @Test
    fun foreach2() {
        "".foreach2 { first, second ->
            failTest("empty cannot be called with first / second char")
        }

        "a".foreach2 { first, second ->
            failTest("single cannot be called with first / second char.")
        }


        "ab".foreach2 { first, second ->
            first.assert('a')
            second.assert('b')
        }

        "abc".foreach2 { first, second ->
            failTest("cannot be called on odd length'ed collections")
        }

        var counterAbAb = 0
        "abab".foreach2 { first, second ->
            first.assert('a')
            second.assert('b')
            counterAbAb += 2
        }
        counterAbAb.assert(4)


    }

    @Test
    fun foreach2Indexed() {
        "".foreach2Indexed { index, first, second ->
            failTest("empty cannot be called with first / second char")
        }

        "a".foreach2Indexed { index, first, second ->
            failTest("single cannot be called with first / second char.")
        }


        "ab".foreach2Indexed { index, first, second ->
            first.assert('a')
            second.assert('b')
        }

        "abc".foreach2Indexed { index, first, second ->
            failTest("cannot be called on odd length'ed collections")
        }

        var counterAbAb = 0
        "abab".foreach2Indexed { index, first, second ->
            first.assert('a')
            second.assert('b')
            counterAbAb += 2
        }
        counterAbAb.assert(4)

    }

    @Ignore
    @Test
    fun fileExtension1() {
    }

    @Ignore
    @Test
    fun skipStartsWith() {
    }

}