package com.commonsense.andorid.kotlin


import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Thread.sleep

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {

        sleep(2000)
        assertEquals(4, (2 + 2).toLong())
    }
}