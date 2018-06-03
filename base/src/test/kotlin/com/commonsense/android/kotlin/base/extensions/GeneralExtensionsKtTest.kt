package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertNotNullAndEquals
import com.commonsense.android.kotlin.test.assertNull
import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Kasper Tvede on 27-05-2018.
 * Purpose:
 */
class GeneralExtensionsKtTest {

    @Test
    fun measureSecondTime() {
    }

    @Test
    fun toEditable() {
    }

    @Test
    fun isNull() {

    }

    @Test
    fun isNotNull() {
    }

    @Test
    fun isNullOrEqualTo() {
    }

    @Test
    fun weakReference() {
    }

    @Test
    fun useOpt() {
    }

    @Test
    fun use() {
    }

    @Test
    fun use1() {
    }

    @Test
    fun weakReference1() {
    }

    @Test
    fun parseTo() {
        var testOpt: String? = null
        val optString: String? = "testMe"

        optString.parseTo { testOpt = it }
        testOpt.assertNotNullAndEquals(optString)


    }

    @Test
    fun map() {
        val optString: String? = ""
        optString.map(42, 0).assert(42, "value is not null")
        val testme: String = ""
        testme.map(100, 0).assert(100, "should not treat correctly typed variables wrong")
        val nullOpt: String? = null
        nullOpt.map(-3, 12).assert(12, "should map null into ifNull branch")
    }

    @Test
    fun mapLazy() {
        val optString: String? = ""
        optString.mapLazy({ 42 }, { 0 }).assert(42, "value is not null")
        val testme: String = ""
        testme.mapLazy({ 100 }, { 0 }).assert(100, "should not treat correctly typed variables wrong")
        val nullOpt: String? = null
        nullOpt.mapLazy({ -3 }, { 12 }).assert(12, "should map null into ifNull branch")
    }


    @Test
    fun forEach() {

        var counter = 0
        5.forEach {
            counter += 1
        }
        counter.assert(5, "should perform the loop 5 times.")
    }

    @Test
    fun castAny() {
        val temp: Any = "test"
        temp.cast<String>().assertNotNullAndEquals("test")
        val temp2: Any = 42
        temp2.cast<String>().assertNull("int is not a string")
    }
}