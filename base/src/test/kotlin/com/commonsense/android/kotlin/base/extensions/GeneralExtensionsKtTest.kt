package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.test.*
import kotlinx.coroutines.experimental.*
import org.junit.*

/**
 * Created by Kasper Tvede on 27-05-2018.
 * Purpose:
 */
class GeneralExtensionsKtTest {

    @Test
    fun testMeasureSecondTime() {
        val empty = measureSecondTime { }
        empty.assert(0, "should not take a second computing nothing..")
        val measureInSeconds = measureSecondTime {
            runBlocking {
                delay(1000)
            }
        }
        measureInSeconds.assert(1, "should be 1 second (at least).")
    }


    @Test
    fun isNull() {
        val opt: Int? = null
        opt.isNull.assert(true)
        val nonOpt: Int? = 42
        nonOpt.isNull.assert(false)
    }

    @Test
    fun isNotNull() {
        val opt: Int? = null
        opt.isNotNull.assert(false)
        val nonOpt: Int? = 42
        nonOpt.isNotNull.assert(true)
    }

    @Test
    fun isNullOrEqualTo() {
        val opt: String? = null
        opt.isNullOrEqualTo("test").assert(true, "is null")
        val nonOpt: String? = "test"
        nonOpt.isNullOrEqualTo("test").assert(true, "is equal")
        nonOpt.isNullOrEqualTo("test2").assert(false, "is not equal to test2")
        opt.isNullOrEqualTo("test2").assert(true, "is still null")
    }

    @Ignore
    @Test
    fun weakReference() {

    }

    @Ignore
    @Test
    fun weakReference1() {

    }


    @Ignore
    @Test
    fun useOpt() {

    }

    @Ignore
    @Test
    fun use() {

    }

    @Ignore
    @Test
    fun use1() {

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
        val testme = ""
        testme.map(100, 0).assert(100, "should not treat correctly typed variables wrong")
        val nullOpt: String? = null
        nullOpt.map(-3, 12).assert(12, "should map null into ifNull branch")
    }

    @Test
    fun mapLazy() {
        val optString: String? = ""
        optString.mapNullLazy({ 42 }, { 0 }).assert(42, "value is not null")
        val testme = ""
        testme.mapNullLazy({ 100 }, { 0 }).assert(100, "should not treat correctly typed variables wrong")
        val nullOpt: String? = null
        nullOpt.mapNullLazy({ -3 }, { 12 }).assert(12, "should map null into ifNull branch")
    }

    @Test
    fun mapLazyAsync() = runBlocking {
        true.mapLazyAsync({ 42 }, { 0 }).assert(42, "value is true")
        false.mapLazyAsync({ -3 }, { 12 }).assert(12, "false should use the second argument")
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

    @Test
    fun useRefOr() {
        val someString = "value"
        val weakRef = someString.weakReference()
        weakRef.useRefOr({
            assert(someString)
        }, {
            failTest("should have valid weak reference")
        })
    }

    @Test
    fun useOr() {
        var optStringCounter = 0
        val optString: String? = null
        optString.useOr({ failTest("null is not a string") }, { optStringCounter += 1 })
        optStringCounter.assert(1, "should run the ifNull Callback")

        var stringCounter = 0
        val stringValue: String? = "magic test"
        stringValue.useOr({ stringCounter += length }, { failTest("magic test is not null") })
        stringCounter.assert(stringValue?.length ?: 0, "should get the right string back.")
    }

    @Ignore
    @Test
    fun measureSecondTime() {
    }

    @Ignore
    @Test
    fun mapNullLazy() {
    }

    @Ignore
    @Test
    fun mapEach() {
    }
}